package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class SqlLog(
    val id: Int,
    /**
     * The time for executed sql. sample: `2024-03-06T06:54:48.829937+00:00`
     */
    @SerializedName("create_time")
    val createTime: String,
    @SerializedName("is_suc")
    val success: Boolean,
    val ip: String,
    val result: String,
    val sql: String
)