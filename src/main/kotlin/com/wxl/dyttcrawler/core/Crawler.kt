package com.wxl.dyttcrawler.core

import com.wxl.dyttcrawler.downloader.HttpDownloader
import com.wxl.dyttcrawler.scheduler.BatchScheduler
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.pipeline.Pipeline
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.scheduler.Scheduler
import java.io.Closeable
import java.io.IOException
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

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
    private val downloader: HttpDownloader,
    private val pipelines: List<Pipeline>,
    private val scheduler: Scheduler,
    private val coroutineScope: CoroutineScope,
    private val startRequests: List<Request>,
    private val exitWhenComplete: Boolean = false,
    private val concurrentNums: Int = 1
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
        lateinit var downloader: HttpDownloader

        /**
         * 结果处理
         */
        lateinit var pipelines: List<Pipeline>

        /**
         * 任务队列
         */
        lateinit var scheduler: Scheduler

        /**
         * 协程scope
         */
        lateinit var coroutineScope: CoroutineScope

        /**
         * 开始的请求
         */
        lateinit var startRequests: List<Request>

        /**
         * 完成时是否结束
         */
        var exitWhenComplete: Boolean = false

        /**
         * 并发度
         */
        var concurrentNums: Int = 1

        /**
         * 监听器
         */
        var crawlerListeners: MutableList<CrawlerListener> = mutableListOf()

        fun build(): Crawler {
            val listeners = crawlerListeners.toTypedArray()
            return Crawler(
                taskId, pageProcessor, downloader,
                pipelines, scheduler, coroutineScope,
                startRequests, exitWhenComplete, concurrentNums
            ).apply {
                addCrawlerListener(*listeners)
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
     * 状态
     */
    private val stat: AtomicReference<CrawlerStatus> = AtomicReference(CrawlerStatus.INIT)

    /**
     * 页面处理计数
     */
    private val pageCount = AtomicLong(0)

    /**
     * 并发控制channel
     */
    private val concurrentChannel = Channel<Int>(concurrentNums)

    @Volatile
    private var crawlerJob: Job? = null

    companion object {
        private val log = LoggerFactory.getLogger(Crawler::class.java)

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
    fun start(): Boolean {
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

        crawlerJob = newCrawlerJob()
        crawlerJob!!.start()

        return true
    }

    /**
     * 创建新的爬虫任务
     */
    private fun newCrawlerJob(): Job =
        coroutineScope.launch(CoroutineName("cp"), start = CoroutineStart.LAZY) {
            try {
                log.info("crawler {} started!", uuid)
                while (isActive && stat.get() == CrawlerStatus.RUNNING) {
                    val request = scheduler.poll(this@Crawler)
                    if (request == null) {
                        val emptyChildrenJob = coroutineContext.job.children.iterator().hasNext()
                        if (emptyChildrenJob && concurrentChannel.isEmpty && exitWhenComplete) {
                            log.info("crawler {} execute complete by empty request", uuid)
                            break
                        }
                        delay(1000)
                    } else {
                        log.debug("crawler {} wait download: {}", uuid, request.url)
                        concurrentChannel.send(0)

                        launch(CoroutineName("ch")) {
                            try {
                                processRequest(request)
                                delay(site.sleepTime.toLong())
                            } finally {
                                concurrentChannel.receive()
                            }
                        }
                    }
                }

                // 等待处理完成
                for (child in coroutineContext.job.children) {
                    child.join()
                }
                log.info("crawler {} closed! {} pages downloaded.", uuid, pageCount.get())

            } finally {
                stat.set(CrawlerStatus.STOPPED)
            }
        }

    /**
     * 请求处理
     */
    private fun processRequest(request: Request) {
        try {
            val page = downloader.download(request, this@Crawler)
            if (page.isDownloadSuccess) {
                onDownloadSuccess(request, page)
            } else {
                onDownloaderFail(request)
            }
        } catch (e: Exception) {
            notifyError(request, e)
            log.error("process request {} error", request, e)
        } finally {
            pageCount.incrementAndGet()
        }
    }

    /**
     * 异步停止
     */
    fun stop() = stat.compareAndSet(CrawlerStatus.RUNNING, CrawlerStatus.STOPPING)

    /**
     * 等待停止
     */
    fun awaitStopped() = runBlocking {
        crawlerJob?.join()
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
        awaitStopped()
        runBlocking {
            concurrentChannel.close()
        }

        for (pipeline in pipelines) {
            destroyEach(pipeline)
        }
        destroyEach(pageProcessor)
        destroyEach(scheduler)
        destroyEach(downloader)
    }

    private fun destroyEach(obj: Any) {
        if (obj is Closeable) {
            try {
                obj.close()
            } catch (e: IOException) {
                log.error("close {} has error", obj, e)
            }
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
            notifySuccess(request)
        } else {
            log.info("page status code error, page {} , code: {}", request.url, page.statusCode)
            notifyError(request)
        }
    }

    /**
     * 失败处理
     */
    private fun onDownloaderFail(request: Request) {
        log.info("page download fail, page {}", request.url)
        notifyError(request)
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
        for (crawlerListener in crawlerListeners) {
            try {
                crawlerListener.onSuccess(request, this)
            } catch (e: Exception) {
                log.error("crawler on success process error:{}", request, e)
            }
        }
    }

    private fun notifyError(request: Request, e: Exception? = null) {
        for (crawlerListener in crawlerListeners) {
            try {
                crawlerListener.onError(request, this, e)
            } catch (e: Exception) {
                log.error("crawler on error process error:{}", request, e)
            }
        }
    }


    override fun getUUID(): String = taskId

    override fun getSite(): Site = site


}


