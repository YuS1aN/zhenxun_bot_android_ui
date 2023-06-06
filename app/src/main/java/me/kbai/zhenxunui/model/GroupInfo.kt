package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/5/31
 */
data class GroupInfo(
    val group: Group,
    val level: Int,
    val status: Boolean,
    @SerializedName("close_plugins")
    val closedPlugins: List<String>,
    val task: List<Task>
) {

    data class Group(
        @SerializedName("group_id")
        val groupId: String,
        @SerializedName("group_name")
        val groupName: String,
        @SerializedName("member_count")
        val memberCount: Int,
        @SerializedName("max_member_count")
        val maxMemberCount: Int
    )

    data class Task(
        val name: String,
        val nameZh: String,
        val status: Boolean
    )

    fun makeUpdateGroup() = UpdateGroup(
        group.groupId,
        status,
        level
    )
}

