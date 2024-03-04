package me.kbai.zhenxunui.repository

import com.google.gson.JsonParseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.kbai.zhenxunui.api.ApiResponse
import me.kbai.zhenxunui.api.BotApi
import me.kbai.zhenxunui.api.RawApiResponse
import me.kbai.zhenxunui.api.TypedWebSocketHolder
import me.kbai.zhenxunui.api.WebSocketHolder
import me.kbai.zhenxunui.model.ChatMessage
import me.kbai.zhenxunui.model.HandleRequest
import me.kbai.zhenxunui.model.PluginDetail
import me.kbai.zhenxunui.model.PluginInfo
import me.kbai.zhenxunui.model.PluginSwitch
import me.kbai.zhenxunui.model.PluginType
import me.kbai.zhenxunui.model.SendMessage
import me.kbai.zhenxunui.model.SystemStatus
import me.kbai.zhenxunui.model.UpdateGroup
import me.kbai.zhenxunui.model.UpdatePlugin
import org.json.JSONObject
import retrofit2.HttpException

/**
 * @author Sean on 2023/6/1
 */
object ApiRepository {

    fun login(username: String, password: String) = networkFlow {
        BotApi.service.login(username, password)
    }

    fun getFriendList(botId: String) = networkFlow { BotApi.service.getFriendList(botId) }

    fun getGroupList(botId: String) = networkFlow { BotApi.service.getGroupList(botId) }

    fun getGroupDetail(botId: String, groupId: String) =
        networkFlow { BotApi.service.getGroupDetail(botId, groupId) }

    fun updateGroup(updateGroup: UpdateGroup) =
        networkFlow { BotApi.service.updateGroup(updateGroup) }

    fun getPluginList(vararg types: PluginType, menuType: String? = null) = networkFlow {
        BotApi.service.getPluginList(
            types.map { it.string }.toTypedArray(),
            menuType
        )
    }

    fun getPluginDetail(pluginInfo: PluginInfo) = networkFlow(PluginDetail(pluginInfo, null)) {
        BotApi.service.getPluginDetail(pluginInfo.module)
    }

    fun updatePlugin(plugin: UpdatePlugin) = networkFlow { BotApi.service.updatePlugin(plugin) }

    fun getPluginMenuTypes() = networkFlow { BotApi.service.getPluginMenuType() }

    fun pluginSwitch(switch: PluginSwitch) = networkFlow { BotApi.service.pluginSwitch(switch) }

    fun getRequestList() = networkFlow { BotApi.service.getRequestList() }

    fun clearRequest(type: String) = networkFlow { BotApi.service.clearRequest(type) }

    fun approveRequest(handle: HandleRequest) =
        networkFlow { BotApi.service.approveRequest(handle) }

    fun refuseRequest(handle: HandleRequest) = networkFlow { BotApi.service.refuseRequest(handle) }

    fun deleteRequest(handle: HandleRequest) = networkFlow { BotApi.service.deleteRequest(handle) }

    fun getDiskUsage() = rawNetworkFlow { BotApi.service.getDiskUsage() }

    fun getStatusList() = rawNetworkFlow { BotApi.service.getStatusList() }

    fun getBotList() = networkFlow { BotApi.service.getBotList() }

    fun getMessageCount(botId: String) = networkFlow { BotApi.service.getBotMessageCount(botId) }

    fun getActiveGroup() = rawNetworkFlow { BotApi.service.getActiveGroup() }

    fun getPopularPlugin() = rawNetworkFlow { BotApi.service.getPopularPlugin() }

    fun sendMessage(message: SendMessage) = networkFlow { BotApi.service.sendMessage(message) }

    fun newSystemStatusWebSocket(
        scope: CoroutineScope,
        onMessage: (message: SystemStatus?, exception: JsonParseException?) -> Unit
    ) = object : TypedWebSocketHolder<SystemStatus>(
        SystemStatus::class.java,
        "system_status",
        scope
    ) {
        override fun onMessage(message: SystemStatus?, exception: JsonParseException?) {
            onMessage.invoke(message, exception)
        }
    }

    fun newBotLogsWebSocket(
        scope: CoroutineScope,
        onMessage: (message: String) -> Unit
    ) = object : WebSocketHolder("logs", scope) {
        override fun onMessage(text: String) {
            onMessage.invoke(text)
        }
    }

    fun newChatWebSocket(
        scope: CoroutineScope,
        onMessage: (message: ChatMessage?, exception: JsonParseException?) -> Unit
    ) = object : TypedWebSocketHolder<ChatMessage>(ChatMessage::class.java, "chat", scope) {
        override fun onMessage(message: ChatMessage?, exception: JsonParseException?) {
            onMessage(message, exception)
        }
    }

    private fun <T> networkFlow(
        tempData: T? = null,
        f: suspend () -> ApiResponse<T>
    ): Flow<Resource<T>> = flow {
        emit(Resource.loading(tempData))
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

    private fun rawNetworkFlow(
        f: suspend () -> RawApiResponse
    ): Flow<Resource<String>> = flow {
        emit(Resource.loading(null))
        val resp: RawApiResponse
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