package me.kbai.zhenxunui.model

data class BotMessageCount(
    val num: Int,
    val day: Int,
    val week: Int,
    val month: Int,
    val year: Int
) {
    constructor() : this(0, 0, 0, 0, 0)
}