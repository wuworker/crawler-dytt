package com.wxl.dyttcrawler.web.error

import com.wxl.dyttcrawler.web.dto.ResultCode
import com.wxl.dyttcrawler.web.dto.ResultDTO
import org.slf4j.LoggerFactory
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.WebRequest

/**
 * Create by wuxingle on 2021/10/13
 * 异常attributes
 */
class DyttErrorAttributes : DefaultErrorAttributes() {

    companion object {
        private val log = LoggerFactory.getLogger(DyttErrorAttributes::class.java)
    }

    override fun getErrorAttributes(webRequest: WebRequest, options: ErrorAttributeOptions): MutableMap<String, Any?> {
        val errorAttributes = super.getErrorAttributes(webRequest, options)

        if (log.isDebugEnabled) {
            log.debug("error message is:{}", errorAttributes)
        }

        val status = HttpStatus.resolve(errorAttributes["status"] as Int)
        if (status == null || status.is5xxServerError) {
            return ResultDTO.fail<Any?>(ResultCode.SERVER_ERROR).toMap().toMutableMap()
        }

        return ResultDTO.fail<Any?>(ResultCode.REQUEST_BAD).toMap().toMutableMap()
    }

}