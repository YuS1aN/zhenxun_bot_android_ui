package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.webkit.internal.ApiFeature.M
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.TableColumn
import me.kbai.zhenxunui.model.TableListItem
import me.kbai.zhenxunui.repository.ApiRepository

class DbManageViewModel : ViewModel() {

    private val _tables: MutableLiveData<List<TableListItem>> = MutableLiveData()
    val tables: LiveData<List<TableListItem>> = _tables

    private val _columnMap: MutableMap<String, List<TableColumn>> = HashMap()

    fun requestTableList() = viewModelScope.launch {
        ApiRepository.getTableList().apiCollect {
            if (it.success()) {
                _tables.value = it.data ?: return@apiCollect
                _columnMap.clear()
            }
        }
    }

    fun getColumns(table: String) = flow {
        val data = _columnMap[table]
        if (data != null) {
            emit(data)
            return@flow
        }
        val res = ApiRepository.getTableColumn(table).apiCollect()
        if (res.success() && res.data != null) {
            _columnMap[table] = res.data
            emit(res.data)
        }
    }
}