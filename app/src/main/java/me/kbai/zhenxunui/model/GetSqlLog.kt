package me.kbai.zhenxunui.model

data class GetSqlLog(
    val index: Int = 1,
    val size: Int = 5
) {
    companion object {
        val DEFAULT = GetSqlLog()
    }
}