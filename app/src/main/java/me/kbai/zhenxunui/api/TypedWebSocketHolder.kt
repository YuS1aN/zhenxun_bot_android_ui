package me.kbai.zhenxunui.api

import com.google.gson.JsonParseException
import kotlinx.coroutines.CoroutineScope
import me.kbai.zhenxunui.Constants

open class TypedWebSocketHolder<T>
    (private val clazz: Class<T>, path: String, scope: CoroutineScope) :
    WebSocketHolder(path, scope) {

    override fun onMessage(text: String) {
        super.onMessage(text)
        var err: JsonParseException? = null
        val obj: T? = try {
            Constants.gson.fromJson(text, clazz)
        } catch (e: JsonParseException) {
            err = e
            null
        }
        onMessage(obj, err)
    }

    open fun onMessage(message: T?, exception: JsonParseException?) {
    }
}