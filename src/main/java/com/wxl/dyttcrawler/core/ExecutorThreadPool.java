package com.wxl.dyttcrawler.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.util.Assert;

import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create by wuxingle on 2020/6/26
 * ExecutorService包装
 */
public class ExecutorThreadPool implements ThreadPool {

    private static final int MAX_THREAD_NUM = 200;

    private ThreadPoolExecutor threadPoolExecutor;

    private int maxThreadNum;

    private AtomicInteger activeThreadNum = new AtomicInteger();

    public ExecutorThreadPool(int maxThreadNum) {
        Assert.isTrue(maxThreadNum > 0 && maxThreadNum <= MAX_THREAD_NUM, "max thread must in (0,200]");
        this.maxThreadNum = maxThreadNum;
        // cache pool
        this.threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                10, TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("crawler-pool-%s").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 任务提交
     */
    @Override
    public synchronized Future<?> execute(Runnable runnable) throws InterruptedException {
        while (activeThreadNum.get() >= maxThreadNum) {
            this.wait();
        }
        activeThreadNum.incrementAndGet();
        return threadPoolExecutor.submit(() -> {
            try {
                runnable.run();
            } finally {
                synchronized (ExecutorThreadPool.this) {
                    activeThreadNum.decrementAndGet();
                    ExecutorThreadPool.this.notify();
                }
            }
        });
    }

    /**
     * 关闭
     */
    @Override
    public void shutdown() throws InterruptedException {
        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
            threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * 是否关闭
     */
    @Override
    public boolean isShutDown() {
        return threadPoolExecutor.isShutdown();
    }

    /**
     * 运行中线程
     */
    @Override
    public int getActiveThreadNum() {
        return activeThreadNum.get();
    }

    /**
     * 总线程
     */
    @Override
    public int getMaxThreadNum() {
        return maxThreadNum;
    }
}
