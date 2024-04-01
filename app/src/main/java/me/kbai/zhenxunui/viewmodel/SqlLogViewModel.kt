package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.SqlLog
import me.kbai.zhenxunui.repository.ApiRepository
import me.kbai.zhenxunui.tool.GlobalToast

class SqlLogViewModel : ViewModel() {
    private val _logs: MutableLiveData<List<SqlLog>> = MutableLiveData()
    val logs: LiveData<List<SqlLog>> = _logs

    init {
        requestSqlLog()
    }

    fun requestSqlLog() = viewModelScope.launch {
        ApiRepository.getSqlLog().apiCollect {
            if (it.success() && it.data != null) {
                _logs.value = it.data.data
            } else {
                GlobalToast.showToast(it.message)
            }
        }
    }
}