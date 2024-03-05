package me.kbai.zhenxunui.viewmodel

import android.text.SpannableStringBuilder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.extends.logE
import me.kbai.zhenxunui.model.BotMessageCount
import me.kbai.zhenxunui.model.SystemStatus
import me.kbai.zhenxunui.repository.ApiRepository
import me.kbai.zhenxunui.tool.AlwaysDifferent
import me.kbai.zhenxunui.tool.Ansi2SpannedHelper

class ConsoleViewModel : ViewModel() {

    private val _systemStatus: MutableLiveData<SystemStatus> = MutableLiveData()
    val systemStatus: LiveData<SystemStatus> = _systemStatus

    private val _messageCount = MutableStateFlow(BotMessageCount())
    val messageCount: StateFlow<BotMessageCount> = _messageCount

    private val _activeGroup: MutableStateFlow<String> = MutableStateFlow("[]")
    val activeGroup: StateFlow<String> = _activeGroup

    private val _popularPlugin: MutableStateFlow<String> = MutableStateFlow("[]")
    val popularPlugin: StateFlow<String> = _popularPlugin

    private val mStatusWebSocketHolder =
        ApiRepository.newSystemStatusWebSocket(viewModelScope) { message, exception ->
            if (message == null) logE(exception!!)
            _systemStatus.value = message ?: return@newSystemStatusWebSocket
        }

    private val _botLogs: MutableStateFlow<AlwaysDifferent<SpannableStringBuilder>> =
        MutableStateFlow(AlwaysDifferent(SpannableStringBuilder()))
    val botLogs: StateFlow<AlwaysDifferent<SpannableStringBuilder>> = _botLogs
    private val mAnsi2SpannedHelper = Ansi2SpannedHelper()
    private var mLogsLines = 0

    private val mLogsWebSocketHolder = ApiRepository.newBotLogsWebSocket(viewModelScope) {
        val builder = _botLogs.value.obj

        if (builder.isNotEmpty()) builder.append("\n")
        builder.append(mAnsi2SpannedHelper.parse(it))
        if (mLogsLines >= 100) {
            builder.delete(0, builder.indexOf('\n') + 1)
        } else {
            mLogsLines++
        }
        _botLogs.value = _botLogs.value
    }

    fun requestMessageCount(botId: String) = viewModelScope.launch {
        ApiRepository.getMessageCount(botId)
            .apiCollect { res ->
                if (res.data == null) return@apiCollect
                _messageCount.update { res.data }
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
        mStatusWebSocketHolder.connect()
        mLogsWebSocketHolder.connect()
    }

    fun closeWebSocket() {
        mStatusWebSocketHolder.close()
        mLogsWebSocketHolder.close()
    }

    override fun onCleared() {
        mStatusWebSocketHolder.cancel()
        mLogsWebSocketHolder.cancel()
    }
}