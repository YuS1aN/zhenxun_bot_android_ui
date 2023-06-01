package me.kbai.zhenxunui.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.kbai.zhenxunui.api.ApiResponse
import me.kbai.zhenxunui.api.BotApi
import me.kbai.zhenxunui.model.PluginType
import org.json.JSONObject
import retrofit2.HttpException

/**
 * @author Sean on 2023/6/1
 */
object ApiRepository {

    fun login(username: String, password: String) = networkFlow {
        BotApi.service.login(username, password)
    }

    fun getPlugins(type: PluginType) = networkFlow { BotApi.service.getPlugins(type.string) }

    private fun <T> networkFlow(
        f: suspend () -> ApiResponse<T>
    ): Flow<Resource<T>> = flow {
        emit(Resource.loading(null))
        val resp: ApiResponse<T>
        try {
            resp = f.invoke()
        } catch (e: Exception) {
            when (e.also { it.printStackTrace() }) {
//                is SocketTimeoutException ->
//                is ConnectException ->
//                is TimeoutException ->
//                is UnknownHostException ->
//                is JsonParseException ->
                is HttpException -> {
                    (e as HttpException).run {
                        val detail = response()
                            ?.errorBody()
                            ?.let { JSONObject(it.string()) }
                            ?.optString("detail")
                        emit(Resource.error(null, detail ?: message(), -1, code()))
                    }
                }

                else -> emit(Resource.error(null, e.message.orEmpty(), -1))
            }
            return@flow
        }
        if (resp.success) {
            emit(Resource.success(resp.data, resp.info, resp.code))
        } else {
            emit(Resource.error(null, resp.info, resp.code))
        }
    }
}