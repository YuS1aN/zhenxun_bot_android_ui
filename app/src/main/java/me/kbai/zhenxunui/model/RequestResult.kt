package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/8
 */
data class RequestResult(
    val oid: String,
    val id: Int,
    val flag: String,
    val nickname: String?,
    val level: Int?,
    val sex: String?,
    val age: Int?,
    @SerializedName("from_")
    val from: String?,
    val comment: String?,
    @SerializedName("invite_group")
    val inviteGroup: Int?,
    @SerializedName("group_name")
    val groupName: String?
)
