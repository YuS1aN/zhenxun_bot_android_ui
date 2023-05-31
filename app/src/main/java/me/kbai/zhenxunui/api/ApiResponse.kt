package me.kbai.zhenxunui.api

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/5/31
 */
data class ApiResponse<T>(
    @SerializedName("suc")
    val success: Boolean,
    val code: Int,
    val info: String,
    val data: T
)