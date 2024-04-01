package me.kbai.zhenxunui.model

data class SqlLogPage(
    val total: Int,
    val data: List<SqlLog>
)