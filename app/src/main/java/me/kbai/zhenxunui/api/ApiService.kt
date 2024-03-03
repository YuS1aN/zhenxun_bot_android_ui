package me.kbai.zhenxunui.api

import me.kbai.zhenxunui.model.BotBaseInfo
import me.kbai.zhenxunui.model.BotMessageCount
import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.HandleRequest
import me.kbai.zhenxunui.model.LoginInfo
import me.kbai.zhenxunui.model.PluginDetail
import me.kbai.zhenxunui.model.PluginInfo
import me.kbai.zhenxunui.model.PluginSwitch
import me.kbai.zhenxunui.model.BotRequest
import me.kbai.zhenxunui.model.FriendListItem
import me.kbai.zhenxunui.model.GroupListItem
import me.kbai.zhenxunui.model.RequestListResult
import me.kbai.zhenxunui.model.SendMessage
import me.kbai.zhenxunui.model.UpdateGroup
import me.kbai.zhenxunui.model.UpdatePlugin
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author Sean on 2023/5/31
 */
interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<LoginInfo>

    @GET("get_group")
    suspend fun getGroup(): ApiResponse<List<GroupInfo>>

    @GET("manage/get_friend_list")
    suspend fun getFriendList(@Query("bot_id") botId: String): ApiResponse<List<FriendListItem>>

    @GET("manage/get_group_list")
    suspend fun getGroupList(@Query("bot_id") botId: String): ApiResponse<List<GroupListItem>>

    @POST("update_group")
    suspend fun updateGroup(@Body updateGroup: UpdateGroup): ApiResponse<Unit>

    @GET("plugin/get_plugin_list")
    suspend fun getPluginList(
        @Query("plugin_type") type: String,
        @Query("menu_type") menuType: String? = null
    ): ApiResponse<List<PluginInfo>>

    @GET("plugin/get_plugin")
    suspend fun getPluginDetail(@Query("module") module: String): ApiResponse<PluginDetail>

    @POST("plugin/update_plugin")
    suspend fun updatePlugin(@Body updatePlugin: UpdatePlugin): ApiResponse<Unit>

    @GET("plugin/get_plugin_menu_type")
    suspend fun getPluginMenuType(): ApiResponse<List<String>>

    @POST("plugin/change_switch")
    suspend fun pluginSwitch(@Body switch: PluginSwitch): ApiResponse<Unit>

    @GET("manage/get_request_list")
    suspend fun getRequestList(): ApiResponse<RequestListResult>

    @DELETE("manage/clear_request")
    suspend fun clearRequest(@Query("request_type") type: String): ApiResponse<Unit>

    @POST("manage/approve_request")
    suspend fun approveRequest(@Body handleRequest: HandleRequest): ApiResponse<Unit>

    @POST("manage/refuse_request")
    suspend fun refuseRequest(@Body handleRequest: HandleRequest): ApiResponse<Unit>

    @POST("manage/delete_request")
    suspend fun deleteRequest(@Body handleRequest: HandleRequest): ApiResponse<Unit>

    @GET("system/disk")
    suspend fun getDiskUsage(): RawApiResponse

    @GET("system/statusList")
    suspend fun getStatusList(): RawApiResponse

    @GET("main/get_base_info")
    suspend fun getBotList(): ApiResponse<List<BotBaseInfo>>

    @GET("main/get_all_ch_count")
    suspend fun getBotMessageCount(@Query("bot_id") botId: String): ApiResponse<BotMessageCount>

    @GET("main/get_active_group")
    suspend fun getActiveGroup(): RawApiResponse

    @GET("main/get_hot_plugin")
    suspend fun getPopularPlugin(): RawApiResponse

    @POST("manage/send_message")
    suspend fun sendMessage(@Body message: SendMessage): ApiResponse<*>
}