package me.kbai.zhenxunui.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.extends.logE
import me.kbai.zhenxunui.extends.logI
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

open class WebSocketHolder(private val path: String, private val scope: CoroutineScope) {

    private var mWebSocket: WebSocket? = null
    private var mCanceled = false

    fun connect() {
        if (mCanceled) return

        mWebSocket = BotApi.openWebSocket(path, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                logI("WebSocket is connected.")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                logI("WebSocket is closed.")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                logI("WebSocket is closing: $reason")
                webSocket.close(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                logE("WebSocket on failure: $response \n $t")
                webSocket.cancel()
                scope.launch {
                    delay(10_000)
                    connect()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                logI("WebSocket receives a string message: $text")
                scope.launch { onMessage(text) }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                logI("WebSocket receives a byte string message, len: ${bytes.size}")
                scope.launch { onMessage(bytes) }
            }
        })
    }

    open fun onMessage(text: String) {}

    open fun onMessage(bytes: ByteString) {}

    fun send(text: String) = mWebSocket?.send(text)

    fun close(code: Int, reason: String?) = mWebSocket?.close(code, reason)

    fun cancel() {
        mWebSocket?.cancel()
        mCanceled = true
    }
}