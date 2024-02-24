package me.kbai.zhenxunui.viewmodel

import android.graphics.Color
import android.graphics.Typeface
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import androidx.core.text.toSpannable
import androidx.core.text.toSpanned
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
import me.kbai.zhenxunui.model.BotBaseInfo
import me.kbai.zhenxunui.model.BotMessageCount
import me.kbai.zhenxunui.model.SystemStatus
import me.kbai.zhenxunui.repository.ApiRepository
import me.kbai.zhenxunui.tool.AlwaysDifferent
import me.kbai.zhenxunui.tool.Ansi2SpannedHelper
import pk.ansi4j.core.DefaultFunctionFinder
import pk.ansi4j.core.DefaultParserFactory
import pk.ansi4j.core.DefaultTextHandler
import pk.ansi4j.core.api.Environment
import pk.ansi4j.core.iso6429.C0ControlFunctionHandler
import pk.ansi4j.core.iso6429.C1ControlFunctionHandler
import pk.ansi4j.core.iso6429.ControlSequenceHandler
import pk.ansi4j.core.iso6429.ControlStringHandler
import pk.ansi4j.core.iso6429.IndependentControlFunctionHandler

class ConsoleViewModel : ViewModel() {

    private val _systemStatus: MutableLiveData<SystemStatus> = MutableLiveData()
    val systemStatus: LiveData<SystemStatus> = _systemStatus

    private val _botList: MutableLiveData<List<BotBaseInfo>> = MutableLiveData()
    val botList: LiveData<List<BotBaseInfo>> = _botList

    private val _messageCount = MutableStateFlow(BotMessageCount())
    val messageCount: StateFlow<BotMessageCount> = _messageCount

    private val _activeGroup: MutableStateFlow<String> = MutableStateFlow("[]")
    val activeGroup: StateFlow<String> = _activeGroup

    private val _popularPlugin: MutableStateFlow<String> = MutableStateFlow("[]")
    val popularPlugin: StateFlow<String> = _popularPlugin

    private val mStatusWebSocketHolder =
        ApiRepository.newSystemStatusWebSocket(viewModelScope) { message, exception ->
            if (message == null) {
                logE(exception!!)
                return@newSystemStatusWebSocket
            }
            _systemStatus.value = message
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

    fun requestBotList() = viewModelScope.launch {
        ApiRepository.getBotList()
            .apiCollect {
                _botList.value = it.data ?: return@apiCollect
            }
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