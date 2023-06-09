package me.kbai.zhenxunui.api

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/10
 */
data class RawApiResponse(
    @SerializedName("suc")
    val success: Boolean,
    val code: Int,
    val info: String,
    @JsonAdapter(RawStringJsonAdapter::class)
    val data: String
)