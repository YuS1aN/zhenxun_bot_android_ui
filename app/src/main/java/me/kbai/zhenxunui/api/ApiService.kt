package me.kbai.zhenxunui.api

import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.LoginInfo
import me.kbai.zhenxunui.model.PluginData
import me.kbai.zhenxunui.model.UpdateConfig
import me.kbai.zhenxunui.model.UpdateGroup
import me.kbai.zhenxunui.model.UpdatePlugin
import retrofit2.http.Body
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

    @POST("update_group")
    suspend fun updateGroup(@Body updateGroup: UpdateGroup): ApiResponse<Unit>

    @GET("get_plugins")
    suspend fun getPlugins(@Query("plugin_type") type: String): ApiResponse<List<PluginData>>

    @POST("update_plugins")
    suspend fun updatePlugin(@Body updatePlugin: UpdatePlugin): ApiResponse<Unit>

    @POST("update_config")
    suspend fun updateConfig(@Body updateConfigs: List<UpdateConfig>): ApiResponse<Unit>
}