package me.kbai.zhenxunui

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import me.kbai.zhenxunui.api.ErrorHandleAdapterFactory

/**
 * @author Sean on 2023/5/31
 */
object Constants {
    const val API_PREFIX = "/zhenxun/api/"
    const val WS_PREFIX = "/zhenxun/socket/"

    private val apiBaseUrl_: MutableLiveData<String> = MutableLiveData()
    val apiBaseUrl: LiveData<String> = apiBaseUrl_

    private lateinit var mApplication: Application

    const val SP_NAME = "zhenxunui"
    const val SP_KEY_API = "api"

    val gson = GsonBuilder()
        .registerTypeAdapterFactory(ErrorHandleAdapterFactory())
        .create()

    @JvmStatic
    fun init(app: Application) {
        mApplication = app

        mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .getString(SP_KEY_API, "")
            ?.let { url ->
                apiBaseUrl_.value = url
            }
    }

    fun updateApiUrl(url: String): Boolean {
        if (!Patterns.WEB_URL.matcher(url).find()) return false

        apiBaseUrl_.value = url
        mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(SP_KEY_API, url)
            .apply()

        return true
    }
}