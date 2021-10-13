package com.wxl.dyttcrawler.core

import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.Future
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
 * Create by wuxingle on 2021/10/08
 * ExecutorService包装
 * todo
 */
class ExecutorThreadPool(override val maxThreadNum: Int) : ThreadPool {

    private val threadPoolExecutor = ThreadPoolExecutor(
        0, Integer.MAX_VALUE,
        10, TimeUnit.MINUTES,
        SynchronousQueue(),
        ThreadFactoryBuilder().setDaemon(true).setNameFormat("crawler-pool-%s").build(),
        ThreadPoolExecutor.AbortPolicy()
    )

    private val activeThread = AtomicInteger()

    private val lock = ReentrantLock()

    private val condition = lock.newCondition()

    /**
     * 任务提交
     * 阻塞到提交成功
     */
    override fun execute(runnable: Runnable): Future<*> {
        lock.lock()
        try {
            while (activeThread.get() >= maxThreadNum) {
                condition.await()
            }
            activeThread.incrementAndGet()
            return threadPoolExecutor.submit {
                try {
                    runnable.run()
                } finally {
                    lock.lock()
                    try {
                        activeThread.decrementAndGet()
                        condition.signal()
                    } finally {
                        lock.unlock()
                    }
                }
            }
        } finally {
            lock.unlock()
        }
    }

    /**
     * 关闭
     * 阻塞到关闭结束
     */
    override fun shutdown() {
        threadPoolExecutor.shutdown()
        while (!threadPoolExecutor.isTerminated) {
            threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS)
        }
    }

    /**
     * 是否关闭
     */
    override val isShutDown: Boolean
        get() = threadPoolExecutor.isShutdown

    /**
     * 运行中线程数
     */
    override val activeThreadNum: Int
        get() = activeThread.get()
}
