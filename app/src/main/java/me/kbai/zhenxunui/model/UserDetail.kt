package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class UserDetail(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("ava_url")
    val avatarUrl: String,
    val nickname: String,
    val remark: String,
    @SerializedName("is_ban")
    val banned: Boolean,
    @SerializedName("chat_count")
    val chatCount: Int,
    @SerializedName("call_count")
    val callCount: Int,
    @SerializedName("like_plugin")
    val favouritePlugins: Map<String, Int>
)