package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParseException
import me.kbai.zhenxunui.api.TypedWebSocketHolder
import me.kbai.zhenxunui.ext.apiCollect
import me.kbai.zhenxunui.ext.logE
import me.kbai.zhenxunui.model.BotBaseInfo
import me.kbai.zhenxunui.model.SystemStatus
import me.kbai.zhenxunui.repository.ApiRepository

class ConsoleViewModel : ViewModel() {

    private val _systemStatus: MutableLiveData<SystemStatus> = MutableLiveData()
    val systemStatus: LiveData<SystemStatus> = _systemStatus

    private val _botList: MutableLiveData<List<BotBaseInfo>> = MutableLiveData()
    val botList: LiveData<List<BotBaseInfo>> = _botList

    suspend fun requestBotList() = ApiRepository.getBotList()
        .apiCollect {
            _botList.value = it.data ?: return@apiCollect
        }

    private val mWebSocketHolder =
        object : TypedWebSocketHolder<SystemStatus>(
            SystemStatus::class.java,
            "system_status",
            viewModelScope
        ) {
            override fun onMessage(message: SystemStatus?, exception: JsonParseException?) {
                if (message == null) {
                    logE(exception!!)
                    return
                }
                _systemStatus.value = message
            }
        }

    fun openWebSocket() {
        mWebSocketHolder.connect()
    }

    fun closeWebSocket() {
        mWebSocketHolder.close(1001, null)
    }

    override fun onCleared() {
        mWebSocketHolder.cancel()
    }
}