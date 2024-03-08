package me.kbai.zhenxunui

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.ToNumberStrategy
import me.kbai.zhenxunui.api.ErrorHandleAdapterFactory
import me.kbai.zhenxunui.model.BotBaseInfo

/**
 * @author Sean on 2023/5/31
 */
object Constants {
    const val API_PREFIX = "/zhenxun/api/"
    const val WS_PREFIX = "/zhenxun/socket/"

    private val apiBaseUrl_: MutableLiveData<String> = MutableLiveData()
    val apiBaseUrl: LiveData<String> = apiBaseUrl_

    private lateinit var mApplication: Application

    const val SP_NAME_CONFIG = "config"
    private const val SP_KEY_API = "key0"
    const val SP_KEY_USERNAME = "key1"
    const val SP_KEY_PASSWORD = "key2"

    val gson: Gson = GsonBuilder()
        .registerTypeAdapterFactory(ErrorHandleAdapterFactory())
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()

    var currentBot: BotBaseInfo? = null

    @JvmStatic
    fun init(app: Application) {
        mApplication = app

        mApplication.getSharedPreferences(SP_NAME_CONFIG, Context.MODE_PRIVATE)
            .getString(SP_KEY_API, "")
            ?.let { url ->
                apiBaseUrl_.value = url
            }
    }

    fun updateApiUrl(url: String): Boolean {
        if (!Patterns.WEB_URL.matcher(url).find()) return false

        apiBaseUrl_.value = url
        mApplication.getSharedPreferences(SP_NAME_CONFIG, Context.MODE_PRIVATE)
            .edit()
            .putString(SP_KEY_API, url)
            .apply()

        return true
    }
}