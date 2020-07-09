package com.wxl.crawlerdytt.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.assertj.core.util.Lists;
import org.springframework.util.Assert;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.utils.UrlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Create by wuxingle on 2020/6/7
 * 爬虫门面
 *
 * @see us.codecraft.webmagic.Spider
 */
@Slf4j
public class Crawler implements Closeable, Task {

    /**
     * 状态
     */
    public enum Status {
        INIT(0), RUNNING(1), STOPPING(2), STOPPED(3);

        Status(int value) {
            this.value = value;
        }

        private int value;

        int getValue() {
            return value;
        }
    }


    /**
     * 任务uuid
     */
    private String taskId;

    /**
     * 页面处理
     */
    private PageProcessor pageProcessor;

    /**
     * 页面下载
     */
    private Downloader downloader;

    /**
     * 结果处理
     */
    private List<Pipeline> pipelines;

    /**
     * 任务队列
     */
    private Scheduler scheduler;

    /**
     * 执行线程池
     */
    private ThreadPool threadPool;

    /**
     * 开始的请求
     */
    private List<Request> startRequests;

    /**
     * 监听器
     */
    private List<SpiderListener> spiderListeners = new CopyOnWriteArrayList<>();

    /**
     * 完成时是否结束
     */
    private boolean exitWhenComplete;


    private Site site;

    /****************************************内部状态*****************************************************/

    /**
     * 等待新url的检查间隔
     * 30s
     */
    private static final int WAIT_NEW_URL_CHECK_TIME = 30 * 1000;

    /**
     * 状态
     */
    private AtomicReference<Status> stat = new AtomicReference<>(Status.INIT);


    /**
     * 页面处理计数
     */
    private final AtomicLong pageCount = new AtomicLong(0);


    /**
     * 爬虫异步执行线程池
     */
    private ExecutorService crawlerExecutor = new ThreadPoolExecutor(
            0, 5, 10, TimeUnit.MINUTES, new SynchronousQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("crawler-worker-%s").setDaemon(true).build()
    );


    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     */
    Crawler(String taskId,
            PageProcessor pageProcessor,
            Downloader downloader,
            List<Pipeline> pipelines,
            Scheduler scheduler,
            ThreadPool threadPool,
            List<Request> startRequests,
            boolean exitWhenComplete) {
        Assert.notNull(pageProcessor, "page processor can not null");
        Assert.notNull(downloader, "http downloader can not null");
        Assert.notNull(scheduler, "scheduler can not null");
        Assert.isTrue(threadPool != null && !threadPool.isShutDown(), "thread pool must is active");
        Assert.isTrue(CollectionUtils.isNotEmpty(startRequests), "start urls can not empty");
        this.taskId = taskId;
        this.pageProcessor = pageProcessor;
        this.downloader = downloader;
        if (CollectionUtils.isEmpty(pipelines)) {
            this.pipelines = Lists.newArrayList(new ConsolePipeline());
        } else {
            this.pipelines = Lists.newArrayList(pipelines);
        }
        this.scheduler = scheduler;
        this.threadPool = threadPool;
        this.startRequests = Collections.unmodifiableList(startRequests);
        this.exitWhenComplete = exitWhenComplete;
        this.site = pageProcessor.getSite();

    }

    /**
     * 异步启动
     */
    public boolean start() {
        // check running
        while (true) {
            Status statNow = stat.get();
            if (statNow == Status.RUNNING) {
                log.info("crawler {} already started!", getUUID());
                return false;
            }
            if (statNow == Status.STOPPING) {
                log.info("crawler {} is stopping!", getUUID());
                return false;
            }
            if (stat.compareAndSet(statNow, Status.RUNNING)) {
                if (statNow == Status.INIT) {
                    // add first request
                    for (Request startRequest : startRequests) {
                        addRequest(startRequest);
                    }
                }
                break;
            }
        }
        crawlerExecutor.execute(this::doCrawlerSpin);
        return true;
    }

    /**
     * 异步停止
     */
    public boolean stop() {
        return stat.compareAndSet(Status.RUNNING, Status.STOPPING);
    }


    @Override
    public void close() {
        stop();
        try {
            threadPool.shutdown();
        } catch (InterruptedException e) {
            //ignore
        }
        destroyEach(crawlerExecutor);
        for (Pipeline pipeline : pipelines) {
            destroyEach(pipeline);
        }
        destroyEach(pageProcessor);
        destroyEach(scheduler);
        destroyEach(downloader);
    }

    private void destroyEach(Object object) {
        if (object instanceof ExecutorService) {
            ExecutorService executorService = (ExecutorService) object;
            try {
                executorService.shutdown();
                while (!executorService.isTerminated()) {
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                log.error("threadPoll {} has interrupt", executorService);
            }
        } else if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                log.error("close {} has error", object, e);
            }
        }
    }

    /**
     * 爬虫执行逻辑
     */
    private void doCrawlerSpin() {
        log.info("crawler {} started!", getUUID());
        while (!Thread.currentThread().isInterrupted() && stat.get() == Status.RUNNING) {
            final Request request = scheduler.poll(this);
            if (request == null) {
                if (threadPool.getActiveThreadNum() == 0 && exitWhenComplete) {
                    break;
                }
                // wait until new url added
                synchronized (this) {
                    try {
                        this.wait(WAIT_NEW_URL_CHECK_TIME);
                    } catch (InterruptedException e) {
                        log.info("wait new url is interrupted");
                        break;
                    }
                }
            } else {
                try {
                    threadPool.execute(() -> processRequest(request));
                } catch (InterruptedException e) {
                    log.info("submit process request task is interrupted");
                    addRequest(request);
                    break;
                }
            }
        }
        log.info("crawler {} closed! {} pages downloaded.", getUUID(), pageCount.get());
        stat.set(Status.STOPPED);
    }

    /**
     * 请求处理
     */
    private void processRequest(Request request) {
        try {
            Page page = downloader.download(request, this);
            if (page.isDownloadSuccess()) {
                onDownloadSuccess(request, page);
            } else {
                onDownloaderFail(request);
            }
            notifySuccess(request);
        } catch (Exception e) {
            notifyError(request);
            log.error("process request " + request + " error", e);
        } finally {
            pageCount.incrementAndGet();
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    /**
     * 成功处理
     */
    private void onDownloadSuccess(Request request, Page page) {
        if (site.getAcceptStatCode().contains(page.getStatusCode())) {
            pageProcessor.process(page);
            if (CollectionUtils.isNotEmpty(page.getTargetRequests())) {
                for (Request req : page.getTargetRequests()) {
                    addRequest(req);
                }
            }
            if (!page.getResultItems().isSkip()) {
                for (Pipeline pipeline : pipelines) {
                    pipeline.process(page.getResultItems(), this);
                }
            }
        } else {
            log.info("page status code error, page {} , code: {}", request.getUrl(), page.getStatusCode());
        }
        sleep(site.getSleepTime());
    }

    /**
     * 失败处理
     */
    private void onDownloaderFail(Request request) {
        if (site.getCycleRetryTimes() == 0) {
            sleep(site.getSleepTime());
        } else {
            // for cycle retry
            doCycleRetry(request);
        }
    }

    private void doCycleRetry(Request request) {
        Object cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES);
        if (cycleTriedTimesObject == null) {
            addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
        } else {
            int cycleTriedTimes = (Integer) cycleTriedTimesObject;
            cycleTriedTimes++;
            if (cycleTriedTimes < site.getCycleRetryTimes()) {
                addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes));
            }
        }
        sleep(site.getRetrySleepTime());
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.info("crawler sleep is interrupted");
        }
    }

    /**
     * 请求放队列
     */
    private void addRequest(Request request) {
        if (site.getDomain() == null && request != null && request.getUrl() != null) {
            site.setDomain(UrlUtils.getDomain(request.getUrl()));
        }
        scheduler.push(request, this);
    }

    private void notifyError(Request request) {
        if (CollectionUtils.isNotEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                spiderListener.onError(request);
            }
        }
    }

    private void notifySuccess(Request request) {
        if (CollectionUtils.isNotEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                spiderListener.onSuccess(request);
            }
        }
    }

    @Override
    public String getUUID() {
        if (taskId != null) {
            return taskId;
        }
        if (site != null) {
            return site.getDomain();
        }
        taskId = UUID.randomUUID().toString();
        return taskId;
    }


    @Override
    public Site getSite() {
        return site;
    }

    public void addSpiderListener(SpiderListener... listeners) {
        this.spiderListeners.addAll(Arrays.asList(listeners));
    }

    public long getPageCount() {
        return pageCount.get();
    }

    public Status getStatus() {
        return stat.get();
    }
}
