package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class ActiveGroup(
    @SerializedName("group_id")
    val groupId: String,
    val name: String,
    @SerializedName("chat_num")
    val chatNum: Int,
    @SerializedName("ava_img")
    val avatarUrl: String
)