package me.kbai.zhenxunui.api

import me.kbai.zhenxunui.model.BotBaseInfo
import me.kbai.zhenxunui.model.BotMessageCount
import me.kbai.zhenxunui.model.DeleteRemoteFile
import me.kbai.zhenxunui.model.ExecuteSql
import me.kbai.zhenxunui.model.FriendListItem
import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.GroupListItem
import me.kbai.zhenxunui.model.HandleRequest
import me.kbai.zhenxunui.model.LoginInfo
import me.kbai.zhenxunui.model.PluginDetail
import me.kbai.zhenxunui.model.PluginInfo
import me.kbai.zhenxunui.model.PluginSwitch
import me.kbai.zhenxunui.model.RemoteFile
import me.kbai.zhenxunui.model.RenameRemoteFile
import me.kbai.zhenxunui.model.RequestListResult
import me.kbai.zhenxunui.model.SendMessage
import me.kbai.zhenxunui.model.SqlLog
import me.kbai.zhenxunui.model.TableColumn
import me.kbai.zhenxunui.model.TableListItem
import me.kbai.zhenxunui.model.UpdateGroup
import me.kbai.zhenxunui.model.UpdatePlugin
import me.kbai.zhenxunui.model.UserDetail
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

    @GET("manage/get_friend_list")
    suspend fun getFriendList(@Query("bot_id") botId: String): ApiResponse<List<FriendListItem>>

    @GET("manage/get_group_list")
    suspend fun getGroupList(@Query("bot_id") botId: String): ApiResponse<List<GroupListItem>>

    @GET("manage/get_group_detail")
    suspend fun getGroupDetail(
        @Query("bot_id") botId: String,
        @Query("group_id") groupId: String
    ): ApiResponse<GroupInfo>

    @GET("manage/get_friend_detail")
    suspend fun getUserDetail(
        @Query("bot_id") botId: String,
        @Query("user_id") userId: String
    ): ApiResponse<UserDetail>

    @POST("manage/update_group")
    suspend fun updateGroup(@Body updateGroup: UpdateGroup): ApiResponse<Unit>

    @GET("plugin/get_plugin_list")
    suspend fun getPluginList(
        @Query("plugin_type") type: Array<String>,
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

    @GET("database/get_table_list")
    suspend fun getTableList(): ApiResponse<List<TableListItem>>

    @GET("database/get_table_column")
    suspend fun getTableColumn(@Query("table_name") table: String): ApiResponse<List<TableColumn>>

    @GET("database/get_sql_log")
    suspend fun getSqlLog(): ApiResponse<List<SqlLog>>

    @POST("database/exec_sql")
    suspend fun executeSql(@Body sql: ExecuteSql): ApiResponse<List<LinkedHashMap<String, *>>>

    @POST("system/add_file")
    suspend fun createNewFile(@Body file: RemoteFile): ApiResponse<Unit>

    @POST("system/add_folder")
    suspend fun createNewFolder(@Body file: RemoteFile): ApiResponse<Unit>

    @POST("system/rename_file")
    suspend fun renameFile(@Body renameFile: RenameRemoteFile): ApiResponse<Unit>

    @POST("system/rename_folder")
    suspend fun renameFolder(@Body renameFile: RenameRemoteFile): ApiResponse<Unit>

    @POST("system/delete_file")
    suspend fun deleteFile(@Body deleteFile: DeleteRemoteFile): ApiResponse<Unit>

    @POST("system/delete_folder")
    suspend fun deleteFolder(@Body deleteFile: DeleteRemoteFile): ApiResponse<Unit>

    @GET("system/read_file")
    suspend fun readFile(@Query("full_path") path: String): ApiResponse<String>

    @GET("system/get_dir_list")
    suspend fun readDir(@Query("path") path: String): ApiResponse<List<RemoteFile>>
}