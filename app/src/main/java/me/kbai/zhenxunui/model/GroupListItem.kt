package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class GroupListItem(
    @SerializedName("ava_url")
    val avatarUrl: String,
    @SerializedName("group_id")
    val groupId: String,
    @SerializedName("group_name")
    val groupName: String
)
