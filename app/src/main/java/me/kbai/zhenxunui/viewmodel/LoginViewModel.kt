package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.onEach
import me.kbai.zhenxunui.api.BotApi
import me.kbai.zhenxunui.repository.ApiRepository

/**
 * @author Sean on 2023/6/1
 */
class LoginViewModel : ViewModel() {

    fun login(username: String, password: String) = ApiRepository.login(username, password).onEach {
        if (it.success()) BotApi.authorization = it.data?.run { "$tokenType $accessToken" } ?: ""
    }
}