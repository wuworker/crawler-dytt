package com.wxl.dyttcrawler.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

/**
 * Create by wuxingle on 2020/6/26
 */
public class ExecutorThreadPoolTest {

    private static final int MAX_THREAD = 10;

    private static ThreadPoolExecutor executor;

    private static ThreadPool threadPool;

    private CountDownLatch latch = new CountDownLatch(MAX_THREAD + 1);

    private Runnable runnable = () -> {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        latch.countDown();
        System.out.println(Thread.currentThread().getName() + ":end");
    };

    @BeforeAll
    public static void init() {
        executor = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD,
                1, TimeUnit.MINUTES,
                new SynchronousQueue<>(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {


            }
        });
        threadPool = new ExecutorThreadPool(MAX_THREAD);
    }

    @AfterAll
    public static void after() {
        executor.shutdownNow();
    }

    @Test
    public void test1() throws Exception {
        for (int i = 0; i < MAX_THREAD; i++) {
            executor.execute(runnable);
        }
        executor.execute(runnable);

        latch.await();
    }

    @Test
    public void test2() throws Exception {
        for (int i = 0; i < MAX_THREAD; i++) {
            threadPool.execute(runnable);
        }
        threadPool.execute(runnable);

        latch.await();
    }
}