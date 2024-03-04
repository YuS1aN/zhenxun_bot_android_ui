package me.kbai.zhenxunui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.PluginInfo
import me.kbai.zhenxunui.model.PluginType
import me.kbai.zhenxunui.model.UpdateGroup
import me.kbai.zhenxunui.repository.ApiRepository

class EditGroupViewModel : ViewModel() {

    private val _groupInfo: MutableLiveData<GroupInfo> = MutableLiveData()
    val groupInfo: LiveData<GroupInfo> = _groupInfo

    private val _plugins: MutableLiveData<List<PluginInfo>> = MutableLiveData(ArrayList())
    val plugins: LiveData<List<PluginInfo>> = _plugins

    /**
     * 这里 plugins 在 groupInfo 后设置值, 因为插件状态列表需要两者数据
     */
    fun requestGroupInfo(groupId: String) = viewModelScope.launch {
        val groupDeferred = async {
            ApiRepository.getGroupDetail(Constants.currentBot!!.selfId, groupId).apiCollect()
        }
        val pluginDeferred = async {
            ApiRepository.getPluginList(PluginType.NORMAL, PluginType.ADMIN).apiCollect()
        }
        val groupRes = groupDeferred.await()
        val pluginRes = pluginDeferred.await()
        if (groupRes.success()) {
            _groupInfo.value = groupRes.data ?: return@launch
        }
        if (pluginRes.success()) {
            _plugins.value = pluginRes.data ?: return@launch
        }
    }

    fun updateGroup(group: UpdateGroup) = ApiRepository.updateGroup(group)
}