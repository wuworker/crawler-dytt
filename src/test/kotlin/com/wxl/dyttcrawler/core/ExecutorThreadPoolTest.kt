package com.wxl.dyttcrawler.core

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Create by wuxingle on 2021/10/12
 *
 */
class ExecutorThreadPoolTest {

    companion object {

        private const val MAX_THREAD = 10

        private var executor: ThreadPoolExecutor? = null

        private var threadPool: ThreadPool? = null

        @BeforeAll
        fun init() {
            executor = ThreadPoolExecutor(
                MAX_THREAD, MAX_THREAD,
                1, TimeUnit.MINUTES,
                SynchronousQueue()
            ) { _, _ ->
            }

            threadPool = ExecutorThreadPool(MAX_THREAD)
        }

        @AfterAll
        fun after() {
            executor?.shutdown()
            threadPool?.shutdown()
        }
    }

    private val latch = CountDownLatch(MAX_THREAD + 1)

    private val runnable = Runnable {
        Thread.sleep(5000)

        latch.countDown()
        println(Thread.currentThread().name + ":end")
    }

    @Test
    fun test1() {
        for (i in 0..MAX_THREAD) {
            executor!!.execute(runnable)
        }
        executor!!.execute(runnable)

        latch.await()
    }

    @Test
    fun test2() {
        for (i in 0..MAX_THREAD) {
            threadPool!!.execute(runnable)
        }
        threadPool!!.execute(runnable)

        latch.await()
    }


}