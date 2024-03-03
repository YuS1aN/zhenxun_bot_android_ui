package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("object_id")
    val objectId: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("group_id")
    val groupId: String?,

    val message: List<MessageItem>,

    val name: String,

    @SerializedName("ava_url")
    val avatarUrl: String
) {
    data class MessageItem(
        val type: MessageType,
        val msg: String
    )
}

enum class MessageType {
    @SerializedName("text")
    TEXT,

//        @SerializedName("at")
//        AT
}