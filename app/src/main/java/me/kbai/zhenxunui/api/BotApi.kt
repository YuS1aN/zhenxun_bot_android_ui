package me.kbai.zhenxunui.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.TypeAdapters
import me.kbai.zhenxunui.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Sean on 2023/5/31
 */
object BotApi {
    lateinit var service: ApiService
        private set

    var authorization: String = ""

    private val mGson: Gson = createGson()
    private var mOkHttpClient: OkHttpClient? = null

    init {
        Constants.apiBaseUrl.observeForever { baseUrl ->
            if (baseUrl.isNullOrBlank()) return@observeForever
            service = createApiService(
                mOkHttpClient ?: createOkHttpClient().also { mOkHttpClient = it },
                mGson,
                baseUrl + Constants.API_PREFIX
            )
        }
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

    private fun createGson(): Gson = GsonBuilder()
        .registerTypeAdapterFactory(ErrorHandleAdapterFactory())
        .create()

    private fun createApiService(client: OkHttpClient, gson: Gson, baseUrl: String): ApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ApiService::class.java)
}