package me.kbai.zhenxunui.model

data class RequestListResult(
    val friend: List<BotRequest>,
    val group: List<BotRequest>
)
