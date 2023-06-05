package me.kbai.zhenxunui.model

/**
 * @author Sean on 2023/6/6
 */
data class UpdateConfig(
    val module: String,
    var key: String,
    var value: Any?
)