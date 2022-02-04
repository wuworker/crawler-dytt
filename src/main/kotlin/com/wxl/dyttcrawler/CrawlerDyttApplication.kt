package com.wxl.dyttcrawler

/**
 * Create by wuxingle on 2021/9/11
 * main
 * -Dkotlinx.coroutines.debug
 */

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.wxl.dyttcrawler.properties")
class CrawlerDyttApplication


fun main(args: Array<String>) {
    runApplication<CrawlerDyttApplication>(*args)
}

