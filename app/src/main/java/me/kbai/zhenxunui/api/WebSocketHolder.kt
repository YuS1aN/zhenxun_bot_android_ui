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
    companion object {
        private const val NOT_STARTED = 0
        private const val CONNECTING = 1
        private const val CONNECTED = 2
        private const val CLOSING = 3
        private const val CLOSED = 4
        private const val FAILURE = 5
        private const val CANCELED = -1
    }

    private var mWebSocket: WebSocket? = null
    private var mStatus = NOT_STARTED

    fun connect() {
        if (mStatus in CONNECTING .. CLOSING) return

        mStatus = CONNECTING

        mWebSocket = BotApi.openWebSocket(path, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                logI("WebSocket is connected.")
                mStatus = CONNECTED
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                logI("WebSocket is closed.")
                mStatus = CLOSED
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                logI("WebSocket is closing, reason: $reason")
                webSocket.close(code, reason)
                mStatus = CLOSING
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                logE("WebSocket on failure: $response \n $t")
                webSocket.cancel()
                mStatus = FAILURE
                scope.launch {
                    delay(5_000)
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

    fun close(code: Int = 1001, reason: String? = null) = mWebSocket?.close(code, reason)

    fun cancel() {
        mWebSocket?.cancel()
        mStatus = CANCELED
    }
}