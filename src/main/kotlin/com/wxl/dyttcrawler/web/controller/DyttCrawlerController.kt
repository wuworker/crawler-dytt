package com.wxl.dyttcrawler.web.controller

import com.wxl.dyttcrawler.core.CrawlerStatus
import com.wxl.dyttcrawler.web.dto.ResultCode
import com.wxl.dyttcrawler.web.dto.ResultDTO
import com.wxl.dyttcrawler.web.dto.crawler.CrawlerProgress
import com.wxl.dyttcrawler.web.dto.crawler.ManualUrl
import com.wxl.dyttcrawler.web.service.DyttCrawlerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Create by wuxingle on 2021/10/12
 * 爬虫相关接口
 */
@RestController
@RequestMapping("/dytt/crawler")
class DyttCrawlerController {

    @Autowired
    lateinit var dyttCrawlerService: DyttCrawlerService

    /**
     * 手动保存url
     */
    @PostMapping("/save")
    fun saveUrl(@RequestBody manualUrl: ManualUrl): ResultDTO<Boolean> {
        if (manualUrl.url.isNullOrBlank()) {
            return ResultDTO.fail(ResultCode.BAD_PARAMS)
        }
        dyttCrawlerService.crawlUrl(manualUrl.url!!)
        return ResultDTO.ok()
    }

    /**
     * 启动
     */
    @PostMapping("/start")
    fun startCrawler(): ResultDTO<Boolean> {
        val start = dyttCrawlerService.startCrawler()
        return ResultDTO.ok(start)
    }

    /**
     * 停止
     */
    @PostMapping("/stop")
    fun stopCrawler(): ResultDTO<Boolean> {
        val stop = dyttCrawlerService.stopCrawler()
        return ResultDTO.ok(stop)
    }

    /**
     * 状态
     */
    @GetMapping("/status")
    fun statusCrawler(): ResultDTO<CrawlerStatus> {
        val status = dyttCrawlerService.statusCrawler()
        return ResultDTO.ok(status)
    }

    /**
     * 消费进度
     */
    @GetMapping("/progress")
    fun getConsumerProgress(): ResultDTO<CrawlerProgress> {
        val crawlerProgress = dyttCrawlerService.getCrawlerProgress()
        return ResultDTO.ok(crawlerProgress)
    }

    /**
     * 重置消费
     */
    @PostMapping("/reset")
    fun resetConsumerProgress(): ResultDTO<Boolean> {
        val res = dyttCrawlerService.resetCrawlerProgress()
        return ResultDTO.ok(res)
    }
}
