package com.wxl.dyttcrawler.pipeline

import us.codecraft.webmagic.ResultItems
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.pipeline.Pipeline

/**
 * Create by wuxingle on 2021/10/11
 * 对象匹配pipeline
 */
abstract class DyttPipeline<T>(
    private val clazz: Class<T>
) : Pipeline {

    override fun process(resultItems: ResultItems, task: Task) {
        val obj: T = resultItems.get(clazz.name)
        if (obj != null) {
            process(obj, resultItems, task)
        }
    }


    protected abstract fun process(obj: T, resultItems: ResultItems, task: Task)

}
