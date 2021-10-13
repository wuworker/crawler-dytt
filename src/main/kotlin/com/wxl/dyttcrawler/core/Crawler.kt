package com.wxl.dyttcrawler.core

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.wxl.dyttcrawler.scheduler.BatchScheduler
import org.apache.commons.lang3.SerializationUtils
import org.slf4j.LoggerFactory
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.downloader.Downloader
import us.codecraft.webmagic.pipeline.Pipeline
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.scheduler.Scheduler
import java.io.Closeable
import java.io.IOException
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

/**
 * Create by wuxingle on 2021/10/09
 * 爬虫相关类
 */

/**
 * 等待新url的检查间隔
 * 30s
 */
private const val WAIT_NEW_URL_CHECK_TIME = 30 * 1000L

/**
 * 状态类
 */
enum class CrawlerStatus(val value: Int) {
    INIT(0),
    RUNNING(1),
    STOPPING(2),
    STOPPED(3);
}

/**
 * 爬虫启动类
 */
class Crawler private constructor(
    private val taskId: String,
    private val pageProcessor: PageProcessor,
    private val downloader: Downloader,
    private val pipelines: List<Pipeline>,
    private val scheduler: Scheduler,
    private val threadPool: ThreadPool,
    private val startRequests: List<Request>,
    private val exitWhenComplete: Boolean = false,
) : Closeable, Task {

    /**
     * builder
     */
    class CrawlerBuilder {
        /**
         * 任务uuid
         */
        var taskId: String = UUID.randomUUID().toString()

        /**
         * 页面处理
         */
        lateinit var pageProcessor: PageProcessor

        /**
         * 页面下载
         */
        lateinit var downloader: Downloader

        /**
         * 结果处理
         */
        lateinit var pipelines: List<Pipeline>

        /**
         * 任务队列
         */
        lateinit var scheduler: Scheduler

        /**
         * 执行线程池
         */
        lateinit var threadPool: ThreadPool

        /**
         * 开始的请求
         */
        lateinit var startRequests: List<Request>

        /**
         * 完成时是否结束
         */
        var exitWhenComplete: Boolean = false

        /**
         * 监听器
         */
        var crawlerListeners: MutableList<CrawlerListener> = mutableListOf()

        fun build(): Crawler {
            return Crawler(
                taskId, pageProcessor, downloader,
                pipelines, scheduler, threadPool,
                startRequests, exitWhenComplete
            ).apply {
                addCrawlerListener(*crawlerListeners.toTypedArray())
            }
        }
    }

    /**
     * 监听器
     */
    private val crawlerListeners: MutableList<CrawlerListener> = CopyOnWriteArrayList()

    /**
     * site
     */
    private val site: Site = pageProcessor.site

    /****************************************内部状态*****************************************************/

    /**
     * 锁
     */
    private val lock = ReentrantLock()

    private val condition = lock.newCondition()

    /**
     * 状态
     */
    private val stat: AtomicReference<CrawlerStatus> = AtomicReference(CrawlerStatus.INIT)

    /**
     * 页面处理计数
     */
    private val pageCount = AtomicLong(0)

    /**
     * 爬虫主线程池
     */
    private val crawlerExecutor: ExecutorService = ThreadPoolExecutor(
        0, 1, 10, TimeUnit.MINUTES, SynchronousQueue(),
        ThreadFactoryBuilder().setNameFormat("crawler-worker-%s").setDaemon(true).build()
    )

    /**
     * 爬虫主线程future
     */
    @Volatile
    private var crawlerFuture: Future<*>? = null

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        /**
         * 带接收者的函数类型
         */
        inline fun build(block: CrawlerBuilder.() -> Unit): Crawler =
            CrawlerBuilder().apply(block).build()
    }

    /**
     * 手动调用url
     */
    fun crawl(url: String) = processRequest(Request(url))

    /**
     * 异步启动
     */
    fun start(maxCount: Long = Long.MAX_VALUE): Boolean {
        while (true) {
            val statNow = stat.get()
            if (statNow == CrawlerStatus.RUNNING) {
                log.info("crawler {} already started!", uuid)
                return false
            }
            if (statNow == CrawlerStatus.STOPPING) {
                log.info("crawler {} is stopping!", uuid)
                return false
            }
            if (stat.compareAndSet(statNow, CrawlerStatus.RUNNING)) {
                if (startRequests.isNotEmpty()) {
                    addRequest(startRequests)
                }
                break
            }
        }
        crawlerFuture = crawlerExecutor.submit {
            doCrawlerSpin(maxCount)
        }
        return true
    }

    /**
     * 异步停止
     */
    fun stop(): Boolean = stat.compareAndSet(CrawlerStatus.RUNNING, CrawlerStatus.STOPPING)

    /**
     * 等待执行结束
     */
    @Throws(InterruptedException::class)
    fun awaitStopped() {
        if (crawlerFuture != null) {
            while (!crawlerFuture!!.isDone && !crawlerFuture!!.isCancelled) {
                Thread.sleep(5000)
            }
        }
    }

    /**
     * 添加监听
     */
    fun addCrawlerListener(vararg listeners: CrawlerListener) =
        crawlerListeners.addAll(listeners)

    /**
     * 获取页面处理数
     */
    fun getPageCount(): Long = pageCount.get()

    /**
     * 获取当前状态
     */
    fun getStatus(): CrawlerStatus = stat.get()

    override fun close() {
        stop()
        try {
            awaitStopped()
        } catch (e: InterruptedException) {
            //ignore
        }
        try {
            threadPool.shutdown()
        } catch (e: InterruptedException) {
            //ignore
        }
        for (pipeline in pipelines) {
            destroyEach(pipeline)
        }
        destroyEach(pageProcessor)
        destroyEach(scheduler)
        destroyEach(downloader)
    }

    private fun destroyEach(obj: Any) {
        if (obj is ExecutorService) {
            try {
                obj.shutdown()
                while (!obj.isTerminated) {
                    obj.awaitTermination(5, TimeUnit.SECONDS)
                }
            } catch (e: InterruptedException) {
                log.error("threadPoll {} has interrupt", obj)
            }
        } else if (obj is Closeable) {
            try {
                obj.close()
            } catch (e: IOException) {
                log.error("close {} has error", obj, e)
            }
        }
    }

    /**
     * 爬虫执行逻辑
     *
     * @param maxCount 最大爬取页面数
     */
    private fun doCrawlerSpin(maxCount: Long) {
        log.info("crawler {} started!", uuid)
        var count = 0
        val startIndex = pageCount.get()
        while (!Thread.currentThread().isInterrupted
            && stat.get() == CrawlerStatus.RUNNING
            && count++ < maxCount
        ) {
            val request = scheduler.poll(this)
            if (request == null) {
                if (threadPool.activeThreadNum == 0 && exitWhenComplete) {
                    break
                }
                // wait until new url added
                await(WAIT_NEW_URL_CHECK_TIME)
                count--
            } else {
                try {
                    threadPool.execute {
                        processRequest(request)
                    }
                } catch (e: InterruptedException) {
                    log.info("submit process request task is interrupted");
                    addRequest(request)
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }

        while (!Thread.currentThread().isInterrupted
            && threadPool.activeThreadNum != 0
            && pageCount.get() - startIndex < maxCount
        ) {
            await(5000)
        }

        log.info("crawler {} closed! {} pages downloaded.", uuid, pageCount.get())
        stat.set(CrawlerStatus.STOPPED)
    }

    /**
     * wait
     */
    private fun await(timeout: Long) {
        lock.lock()
        try {
            condition.await(timeout, TimeUnit.MICROSECONDS)
        } catch (e: InterruptedException) {
            log.info("wait is interrupted")
            Thread.currentThread().interrupt()
        } finally {
            lock.unlock()
        }
    }

    /**
     * notify
     */
    private fun signalAll() {
        lock.lock()
        try {
            condition.signalAll()
        } finally {
            lock.unlock()
        }
    }

    /**
     * 请求处理
     */
    private fun processRequest(request: Request) {
        try {
            val page = downloader.download(request, this)
            if (page.isDownloadSuccess) {
                onDownloadSuccess(request, page)
            } else {
                onDownloaderFail(request)
            }
            notifySuccess(request)
        } catch (e: Exception) {
            notifyError(request)
            log.error("process request {} error", request, e)
        } finally {
            pageCount.incrementAndGet()
            signalAll()
        }
    }

    /**
     * 成功处理
     */
    private fun onDownloadSuccess(request: Request, page: Page) {
        if (site.acceptStatCode.contains(page.statusCode)) {
            pageProcessor.process(page)
            if (page.targetRequests.isNotEmpty()) {
                addRequest(page.targetRequests)
            }
            if (!page.resultItems.isSkip) {
                for (pipeline in pipelines) {
                    pipeline.process(page.resultItems, this)
                }
            }
        } else {
            log.info("page status code error, page {} , code: {}", request.url, page.statusCode)
        }
        sleep(site.sleepTime)
    }

    /**
     * 失败处理
     */
    private fun onDownloaderFail(request: Request) {
        if (site.cycleRetryTimes == 0) {
            sleep(site.sleepTime)
        } else {
            // for cycle retry
            doCycleRetry(request)
        }
    }

    private fun doCycleRetry(request: Request) {
        val cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES)
        if (cycleTriedTimesObject == null) {
            addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1))
        } else {
            var cycleTriedTimes = cycleTriedTimesObject as Int
            cycleTriedTimes++
            if (cycleTriedTimes < site.cycleRetryTimes) {
                addRequest(
                    SerializationUtils.clone(request).setPriority(0)
                        .putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes)
                )
            }
        }
        sleep(site.retrySleepTime)
    }

    private fun sleep(time: Int) {
        try {
            Thread.sleep(time.toLong())
        } catch (e: InterruptedException) {
            log.info("crawler sleep is interrupted")
        }
    }

    /**
     * 请求放队列
     */
    private fun addRequest(request: Request) {
        scheduler.push(request, this)
    }

    private fun addRequest(requests: List<Request>) {
        if (scheduler is BatchScheduler) {
            scheduler.push(requests, this)
        } else {
            requests.forEach { addRequest(it) }
        }
    }

    private fun notifySuccess(request: Request) {
        if (crawlerListeners.isNotEmpty()) {
            for (crawlerListener in crawlerListeners) {
                try {
                    crawlerListener.onSuccess(request, this)
                } catch (e: Exception) {
                    log.error("crawler on success process error:{}", request, e)
                }
            }
        }
    }

    private fun notifyError(request: Request) {
        if (crawlerListeners.isNotEmpty()) {
            for (crawlerListener in crawlerListeners) {
                try {
                    crawlerListener.onError(request, this)
                } catch (e: Exception) {
                    log.error("crawler on error process error:{}", request, e)
                }
            }
        }
    }


    override fun getUUID(): String = taskId

    override fun getSite(): Site = site


}


