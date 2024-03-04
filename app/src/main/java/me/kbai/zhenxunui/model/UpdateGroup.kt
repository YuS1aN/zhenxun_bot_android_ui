package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/5/31
 */
data class UpdateGroup(
    @SerializedName("group_id")
    val groupId: String,
    val status: Boolean,
    val level: Int,
    @SerializedName("close_plugins")
    val closedPlugins: List<String>,
    val task: List<String>
)
