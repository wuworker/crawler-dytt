package com.wxl.crawlerdytt.web.dto;

/**
 * Create by wuxingle on 2020/5/27
 * 结果码
 */
public enum ResultCode {

    OK(200, "ok")
    ;

    private int code;

    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}



