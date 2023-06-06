package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.onEach
import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.UpdateGroup
import me.kbai.zhenxunui.repository.ApiRepository

/**
 * @author Sean on 2023/6/7
 */
class GroupViewModel : ViewModel() {
    var groupInfo: List<GroupInfo>? = null

    fun getGroup() = ApiRepository.getGroup().onEach {
        if (it.success()) {
            groupInfo = it.data.orEmpty()
        }
    }

    fun updateGroup(updateGroup: UpdateGroup) = ApiRepository.updateGroup(updateGroup)
}