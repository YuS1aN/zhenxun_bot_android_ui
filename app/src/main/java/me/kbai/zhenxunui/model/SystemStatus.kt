package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class SystemStatus(
    val cpu: Float,
    val memory: Float,
    val disk: Float,
    @SerializedName("check_time")
    val checkTime: String
)