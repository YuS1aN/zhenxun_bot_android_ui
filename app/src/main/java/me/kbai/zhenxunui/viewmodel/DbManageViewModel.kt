package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.ExecuteSql
import me.kbai.zhenxunui.model.TableColumn
import me.kbai.zhenxunui.model.TableListItem
import me.kbai.zhenxunui.repository.ApiRepository

class DbManageViewModel : ViewModel() {

    private val _tables: MutableLiveData<List<TableListItem>> = MutableLiveData()
    val tables: LiveData<List<TableListItem>> = _tables

    private val _columnMap: MutableMap<String, List<TableColumn>> = HashMap()

    private val _executeSqlResult: MutableLiveData<List<LinkedHashMap<String, *>>> =
        MutableLiveData()

    @Suppress("UNCHECKED_CAST")
    val executeSqlResult: LiveData<List<Map<String, *>>> =
        _executeSqlResult as LiveData<List<Map<String, *>>>

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

    fun executeSql(sql: String) = ApiRepository.executeSql(ExecuteSql(sql))
        .onEach { res -> res.data?.also { _executeSqlResult.value = it } }
}