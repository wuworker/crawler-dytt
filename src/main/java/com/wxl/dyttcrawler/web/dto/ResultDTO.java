package com.wxl.dyttcrawler.web.dto;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * Create by wuxingle on 2020/5/27
 * web标准输出对象
 */
@Data
public class ResultDTO<T> {

    private int code;

    private String message;

    private T data;

    private Date date;

    private ResultDTO(int code, String message) {
        this(code, message, null);
    }

    private ResultDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.date = new Date();
    }


    public static <T> ResultDTO<T> ok() {
        return create(ResultCode.OK, null);
    }

    public static <T> ResultDTO<T> ok(T data) {
        return create(ResultCode.OK, data);
    }

    public static <T> ResultDTO<T> fail(ResultCode resultCode) {
        return new ResultDTO<>(resultCode.code(), resultCode.message());
    }

    public static <T> ResultDTO<T> create(ResultCode code, T data) {
        return new ResultDTO<>(code.code(), code.message(), data);
    }

    public static Map<String, Object> failMap(ResultCode resultCode) {
        return ImmutableMap.of("code", resultCode.code(),
                "message", resultCode.message(),
                "date", new Date());
    }
}


