package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class EditRemoteFile(
    @SerializedName("full_path")
    val path: String,
    val content: String
)