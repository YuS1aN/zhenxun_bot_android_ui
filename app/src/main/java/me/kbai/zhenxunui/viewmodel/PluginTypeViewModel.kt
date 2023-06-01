package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import me.kbai.zhenxunui.model.PluginType
import me.kbai.zhenxunui.repository.ApiRepository

/**
 * @author Sean on 2023/6/2
 */
class PluginTypeViewModel : ViewModel() {

    fun requestPlugins(type: PluginType) = ApiRepository.getPlugins(type)
}