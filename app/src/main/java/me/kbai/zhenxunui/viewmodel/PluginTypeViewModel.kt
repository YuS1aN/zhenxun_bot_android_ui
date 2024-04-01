package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import me.kbai.zhenxunui.model.PluginInfo
import me.kbai.zhenxunui.model.PluginSwitch
import me.kbai.zhenxunui.model.PluginType
import me.kbai.zhenxunui.repository.ApiRepository

/**
 * @author Sean on 2023/6/2
 */
class PluginTypeViewModel : ViewModel() {
    private val _plugins: MutableStateFlow<MutableList<PluginInfo>> = MutableStateFlow(ArrayList())
    val plugins: StateFlow<List<PluginInfo>> = _plugins

    fun requestPlugins(type: PluginType) = ApiRepository.getPluginList(type).onEach {
        if (it.success() && it.data != null) _plugins.value = it.data.toMutableList()
    }

    fun pluginSwitch(module: String, state: Boolean) =
        ApiRepository.pluginSwitch(PluginSwitch(module, state))

    fun requestPlugin(pluginInfo: PluginInfo) = ApiRepository.getPluginDetail(pluginInfo)

    fun modifyPluginData(position: Int, data: PluginInfo, notify: Boolean = true) {
        val list = _plugins.value
        //后台接口 getPluginDetail version 错误, 固定为 0
        list[position] = data.copy(version = list[position].version)
        if (notify) _plugins.value = list
    }
}