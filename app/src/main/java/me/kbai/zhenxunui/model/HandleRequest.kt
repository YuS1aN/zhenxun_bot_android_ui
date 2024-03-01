package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/8
 */
data class HandleRequest(
    @SerializedName("bot_id")
    val id: String,
    val flag: String,
    @SerializedName("request_type")
    val type: String
)