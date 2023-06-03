package me.kbai.zhenxunui.repository

/**
 * @author Sean on 2023/6/1
 */
data class Resource<T>(
    val status: Status,
    val data: T?,
    val message: String,
    val code: Int,
    val httpStatusCode: Int = 0
    ) {

    enum class Status {
        LOADING, FAIL, SUCCESS
    }

    fun success() = status == Status.SUCCESS

    companion object {
        fun <T> loading(data: T?) = Resource(Status.LOADING, data, "", -1)

        fun <T> success(data: T?, message: String, code: Int) = Resource(Status.SUCCESS, data, message, code, 200)

        fun <T> error(data: T?, message: String, code: Int, httpStatusCode: Int = 0) = Resource(Status.FAIL, data, message, code, httpStatusCode)
    }
}