package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class FriendListItem(
    @SerializedName("ava_url")
    val avatarUrl: String,
    val nickname: String,
    val remark: String,
    @SerializedName("user_id")
    val userId: String
)