package me.kbai.zhenxunui

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @author Sean on 2023/5/31
 */
object Constants {
    const val API_PREFIX = "/zhenxun/api/"

    private val apiBaseUrl_: MutableLiveData<String> = MutableLiveData()
    val apiBaseUrl: LiveData<String> = apiBaseUrl_

    private lateinit var mApplication: Application

    const val SP_NAME = "zhenxunui"
    const val SP_KEY_API = "api"

    @JvmStatic
    fun init(app: Application) {
        mApplication = app

        mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .getString(SP_KEY_API, "")
            ?.let { url ->
                apiBaseUrl_.value = url
            }
    }

    fun updateApiUrl(url: String) {
        apiBaseUrl_.value = url

        mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(SP_KEY_API, url)
            .apply()
    }
}