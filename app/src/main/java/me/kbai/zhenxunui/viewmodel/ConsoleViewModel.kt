package me.kbai.zhenxunui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.api.TypedWebSocketHolder
import me.kbai.zhenxunui.ext.apiCollect
import me.kbai.zhenxunui.ext.logE
import me.kbai.zhenxunui.model.ActiveGroup
import me.kbai.zhenxunui.model.BotBaseInfo
import me.kbai.zhenxunui.model.SystemStatus
import me.kbai.zhenxunui.repository.ApiRepository

class ConsoleViewModel : ViewModel() {

    private val _systemStatus: MutableLiveData<SystemStatus> = MutableLiveData()
    val systemStatus: LiveData<SystemStatus> = _systemStatus

    private val _botList: MutableLiveData<List<BotBaseInfo>> = MutableLiveData()
    val botList: LiveData<List<BotBaseInfo>> = _botList

    private val _activeGroup: MutableStateFlow<String> = MutableStateFlow("")
    val activeGroup: StateFlow<String> = _activeGroup

    private val _popularPlugin: MutableStateFlow<String> = MutableStateFlow("")
    val popularPlugin: StateFlow<String> = _popularPlugin

    private val mWebSocketHolder =
        ApiRepository.newSystemStatusWebSocket(viewModelScope) { message, exception ->
            if (message == null) {
                logE(exception!!)
                return@newSystemStatusWebSocket
            }
            _systemStatus.value = message
        }

    fun requestBotList() = viewModelScope.launch {
        ApiRepository.getBotList()
            .apiCollect {
                _botList.value = it.data ?: return@apiCollect
            }
    }

    fun requestActiveGroup() = viewModelScope.launch {
        ApiRepository.getActiveGroup()
            .apiCollect { res ->
                if (res.data.isNullOrEmpty()) return@apiCollect
                _activeGroup.update { res.data }
            }
    }

    fun requestPopularPlugin() = viewModelScope.launch {
        ApiRepository.getPopularPlugin()
            .apiCollect { res ->
                if (res.data.isNullOrEmpty()) return@apiCollect
                _popularPlugin.update { res.data }
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