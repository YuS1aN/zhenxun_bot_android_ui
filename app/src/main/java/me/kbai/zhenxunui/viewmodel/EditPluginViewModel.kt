package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.PluginDetail
import me.kbai.zhenxunui.model.PluginInfo
import me.kbai.zhenxunui.model.UpdatePlugin
import me.kbai.zhenxunui.repository.ApiRepository
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast

class EditPluginViewModel : ViewModel() {

    private val _pluginDetail: MutableLiveData<Resource<PluginDetail>> = MutableLiveData()
    val pluginDetail: LiveData<Resource<PluginDetail>> = _pluginDetail

    private val _menuTypes: MutableLiveData<List<String>> = MutableLiveData()
    val menuTypes: LiveData<List<String>> = _menuTypes
    fun requestPluginDetail(pluginInfo: PluginInfo) = viewModelScope.launch {
        ApiRepository.getPluginDetail(pluginInfo)
            .apiCollect {
                _pluginDetail.value = it
            }
    }

    fun updatePlugin(updatePlugin: UpdatePlugin) = ApiRepository.updatePlugin(updatePlugin)

    fun requestMenuTypes() = viewModelScope.launch {
        ApiRepository.getPluginMenuTypes()
            .apiCollect {
                if (it.success()) {
                    _menuTypes.value = it.data ?: return@apiCollect
                } else {
                    GlobalToast.showToast(it.message)
                }
            }
    }
}