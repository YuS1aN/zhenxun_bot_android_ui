package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import me.kbai.zhenxunui.model.PluginData
import me.kbai.zhenxunui.model.PluginType
import me.kbai.zhenxunui.model.UpdatePlugin
import me.kbai.zhenxunui.repository.ApiRepository
import me.kbai.zhenxunui.repository.Resource

/**
 * @author Sean on 2023/6/2
 */
class PluginTypeViewModel : ViewModel() {
    private val _plugins: MutableStateFlow<MutableList<PluginData>> = MutableStateFlow(ArrayList())
    val plugins: StateFlow<List<PluginData>> = _plugins

    fun requestPlugins(type: PluginType) = ApiRepository.getPlugins(type).onEach {
        if (it.success() && it.data != null) _plugins.value = it.data.toMutableList()
    }

    fun updatePlugin(plugin: UpdatePlugin) = ApiRepository.updatePlugin(plugin)

    fun requestPlugin(type: PluginType, module: String) = ApiRepository.getPlugins(type)
        .map { res ->
            var newStatus = res.status
            var newData: PluginData? = null
            if (newStatus == Resource.Status.SUCCESS && res.data != null) {
                newData = res.data.find { it.module == module }
                if (newData == null) newStatus = Resource.Status.FAIL
            }
            return@map Resource<PluginData>(
                newStatus,
                newData,
                res.message,
                res.code,
                res.httpStatusCode
            )
        }

    fun modifyPluginData(position: Int, data: PluginData, notify: Boolean = true) {
        val list = _plugins.value
        list[position] = data
        if (notify) _plugins.value = list
    }
}