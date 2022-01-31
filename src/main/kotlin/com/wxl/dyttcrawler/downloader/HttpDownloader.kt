package com.wxl.dyttcrawler.downloader

import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task

/**
 * Create by wuxingle on 2022/1/8
 * http下载接口
 */
interface HttpDownloader {

    /**
     * 下载
     */
    fun download(request: Request, task: Task): Page

}