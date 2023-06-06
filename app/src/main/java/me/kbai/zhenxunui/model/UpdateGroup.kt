package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/5/31
 */
data class UpdateGroup(
    @SerializedName("group_id")
    val groupId: String,
    var status: Boolean,
    var level: Int
)
