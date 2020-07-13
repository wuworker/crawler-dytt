package com.wxl.crawlerdytt.web.error;

import com.wxl.crawlerdytt.web.dto.ResultCode;
import com.wxl.crawlerdytt.web.dto.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Create by wuxingle on 2020/7/12
 * 异常attributes
 */
@Slf4j
public class DyttErrorAttributes extends DefaultErrorAttributes {


    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

        if (log.isDebugEnabled()) {
            log.debug("error message is:{}", errorAttributes);
        }

        HttpStatus status = HttpStatus.resolve((Integer) errorAttributes.get("status"));
        if (status == null || status.is5xxServerError()) {
            return ResultDTO.failMap(ResultCode.SERVER_ERROR);
        }

        return ResultDTO.failMap(ResultCode.REQUEST_BAD);
    }


}
