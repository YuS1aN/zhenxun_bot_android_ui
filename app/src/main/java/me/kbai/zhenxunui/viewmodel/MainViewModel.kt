package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.BotBaseInfo
import me.kbai.zhenxunui.repository.ApiRepository

class MainViewModel : ViewModel() {
    private val _botList: MutableLiveData<List<BotBaseInfo>> = MutableLiveData()
    val botList: LiveData<List<BotBaseInfo>> = _botList

    private val _currentBot: MutableLiveData<BotBaseInfo> = MutableLiveData()
    val currentBot: LiveData<BotBaseInfo> = _currentBot

    fun requestBotList() = viewModelScope.launch {
        ApiRepository.getBotList()
            .apiCollect { res ->
                _botList.value = res.data ?: return@apiCollect

                res.data
                    .find { it.isSelect }
                    ?.let {
                        _currentBot.value = it
                        Constants.currentBot = it
                    }
            }
    }

    fun selectBot(id: String) {
        val bot = _botList.value!!.find { it.selfId == id } ?: return
        if (_currentBot.value == bot) return

        _currentBot.value = bot
        Constants.currentBot = bot
    }
}