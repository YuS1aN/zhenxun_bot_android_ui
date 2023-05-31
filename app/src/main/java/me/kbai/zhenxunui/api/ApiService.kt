package me.kbai.zhenxunui.api

import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.LoginInfo
import me.kbai.zhenxunui.model.UpdateGroup
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

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
    suspend fun updateGroup(@Body updateGroup: UpdateGroup): ApiResponse<Any>
}