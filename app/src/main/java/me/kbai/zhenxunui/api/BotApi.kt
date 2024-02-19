package me.kbai.zhenxunui.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.kbai.zhenxunui.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Sean on 2023/5/31
 */
object BotApi {
    @Volatile
    lateinit var service: ApiService
        private set

    var authorization: String = ""

    private val mOkHttpClient = createOkHttpClient()

    private val mSchemeRegex = Regex(".+://")

    init {
        Constants.apiBaseUrl.observeForever { baseUrl ->
            if (baseUrl.isNullOrBlank()) return@observeForever
            service = createApiService(
                mOkHttpClient,
                Constants.gson,
                baseUrl + Constants.API_PREFIX
            )
        }
    }

    fun openWebSocket(path: String, listener: WebSocketListener): WebSocket? {
        return (Constants.apiBaseUrl.value ?: return null)
            .replaceFirst(mSchemeRegex, "ws://")
            .plus(Constants.WS_PREFIX + path)
            .let { Request.Builder().url(it).build() }
            .let { mOkHttpClient.newWebSocket(it, listener) }
    }

    private fun createOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain: Interceptor.Chain ->
            val request = chain.request().newBuilder()
                .apply {
                    if (authorization.isNotBlank()) header("Authorization", authorization)
                }
                .build()
            chain.proceed(request)
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private fun createApiService(client: OkHttpClient, gson: Gson, baseUrl: String): ApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ApiService::class.java)
}