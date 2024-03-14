package me.kbai.zhenxunui.model

data class Consumable<T>(
    private var _data: T?,
    private var _id: Int = 0,
    private var _consumed: Boolean = false
) {
    val isConsumed: Boolean
        get() = _consumed

    val id: Int
        get() = _id

    fun get(): T? {
        val data = _data
        _data = null
        _consumed = true
        return data
    }
}