package me.kbai.zhenxunui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @author Sean on 2023/5/31
 */
object Constants {
    private const val API_PREFIX = "/zhenxun/api/"

    private val apiBaseUrl_: MutableLiveData<String> = MutableLiveData()
    val apiBaseUrl: LiveData<String> = apiBaseUrl_

    fun updateApiUrl(url: String) {
        apiBaseUrl_.value = url + API_PREFIX
    }
}