package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class SendMessage(
    @SerializedName("bot_id")
    val botId: String,
    @SerializedName("user_id")
    val userId:String? = null,
    @SerializedName("group_id")
    val groupId: String? = null,
    val message: String
)