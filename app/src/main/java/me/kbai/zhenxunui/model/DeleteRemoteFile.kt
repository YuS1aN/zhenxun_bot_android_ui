package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class DeleteRemoteFile(
    @SerializedName("full_path")
    val path: String
)