package com.wxl.dyttcrawler.web.dto

import java.util.*

/**
 * Create by wuxingle on 2021/10/13
 * web返回结果
 */

/**
 * 错误码
 */
enum class ResultCode(
    val code: Int,
    val message: String
) {
    OK(200, "ok"),

    SCHEDULE_IS_NOT_MONITOR(305, "无法获取消费进度"),
    SCHEDULE_CANNOT_RESET(306, "无法重置消费进度"),


    REQUEST_BAD(400, "请求非法"),
    BAD_PARAMS(401, "请求参数错误"),
    RESULT_NOT_FOUND(404, "找不到结果"),


    SERVER_ERROR(500, "服务器内部错误");
}

/**
 * web标准输出对象
 */
class ResultDTO<T> private constructor(
    var code: Int,
    var message: String,
    var data: T? = null,
    var time: Date = Date()
) {


    companion object {

        fun <T> ok(data: T? = null) = create(ResultCode.OK, data)

        fun <T> fail(code: ResultCode) = create<T>(code)

        fun <T> create(code: ResultCode, data: T? = null): ResultDTO<T> =
            ResultDTO(code.code, code.message, data)
    }

    fun toMap(): Map<String, Any?> =
        mapOf(
            "code" to code,
            "message" to message,
            "data" to data,
            "time" to time.time
        )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResultDTO<*>

        if (code != other.code) return false
        if (message != other.message) return false
        if (data != other.data) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + message.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + time.hashCode()
        return result
    }

    override fun toString(): String {
        return "ResultDTO(code=$code, message='$message', data=$data, time=$time)"
    }


}

