package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.ext.apiCollect
import me.kbai.zhenxunui.repository.ApiRepository

/**
 * @author Sean on 2023/6/10
 */
class InfoViewModel : ViewModel() {
    private val _statusList: MutableStateFlow<String> = MutableStateFlow("")
    val statusList: StateFlow<String> = _statusList

    private val _diskUsage: MutableStateFlow<String> = MutableStateFlow("")
    val diskUsage: StateFlow<String> = _diskUsage

    init {
        viewModelScope.launch {
            while (_diskUsage.value.isBlank()) {
                requestDiskUsage()
                delay(5000)
            }
        }
    }

    private suspend fun requestStatusList() = ApiRepository.getStatusList()
        .apiCollect { res ->
            if (res.data.isNullOrBlank()) return@apiCollect
            _statusList.update { res.data }
        }

    suspend fun pollingStatusList() {
        while (true) {
            requestStatusList()
            delay(5000)
        }
    }

    private suspend fun requestDiskUsage() = ApiRepository.getDiskUsage()
        .apiCollect { res ->
            if (res.data.isNullOrBlank()) return@apiCollect
            _diskUsage.update { res.data }
        }
}