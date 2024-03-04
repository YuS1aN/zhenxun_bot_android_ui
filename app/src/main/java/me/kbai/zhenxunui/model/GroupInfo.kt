package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/5/31
 */
data class GroupInfo(
    @SerializedName("group_id")
    val groupId: String,

    val name: String,

    @SerializedName("call_count")
    val callCount: Int,

    @SerializedName("chat_count")
    val chatCount: Int,

    val level: Int,

    val status: Boolean,

    @SerializedName("ava_url")
    val avatarUrl: String,

    @SerializedName("max_member_count")
    val maxMemberCount: Int,

    @SerializedName("member_count")
    val memberCount: Int,

    @SerializedName("like_plugin")
    val favouritePlugins: Map<String, Int>,

    @SerializedName("close_plugins")
    val closedPlugins: List<String>,

    val task: List<Task>
) {

    data class Task(
        val name: String,
        @SerializedName("zh_name")
        val zhName: String,
        val status: Boolean
    )
}

