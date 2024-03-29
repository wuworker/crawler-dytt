package com.wxl.dyttcrawler.web.dto;

/**
 * Create by wuxingle on 2020/5/27
 * 结果码
 */
public enum ResultCode {

    OK(200, "ok"),

    SCHEDULE_IS_NOT_MONITOR(305, "无法获取消费进度"),
    SCHEDULE_CANNOT_RESET(306, "无法重置消费进度"),


    REQUEST_BAD(400, "请求非法"),
    BAD_PARAMS(401, "请求参数错误"),
    RESULT_NOT_FOUND(404, "找不到结果"),


    SERVER_ERROR(500, "服务器内部错误");

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



