package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class RenameRemoteFile(
    @SerializedName("name")
    val newName: String,
    @SerializedName("old_name")
    val oldName: String,
    val parent: String?
)