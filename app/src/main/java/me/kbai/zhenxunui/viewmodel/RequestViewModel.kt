package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEach
import me.kbai.zhenxunui.model.HandleRequest
import me.kbai.zhenxunui.model.BotRequest
import me.kbai.zhenxunui.repository.ApiRepository

/**
 * @author Sean on 2023/6/8
 */
class RequestViewModel : ViewModel() {
    var friendRequests: List<BotRequest>? = null
    var groupRequests: List<BotRequest>? = null

    fun getRequest() = ApiRepository.getRequestList().onEach {
        if (it.success() && it.data != null) {
            friendRequests = it.data.friend
            groupRequests = it.data.group
        }
    }

    fun clearRequest(type: String) = ApiRepository.clearRequest(type)

    fun handleRequest(handle: String, info: HandleRequest) = when (handle) {
        "approve" -> ApiRepository.approveRequest(info)
        "refuse" -> ApiRepository.refuseRequest(info)
        "delete" -> ApiRepository.deleteRequest(info)
        else -> emptyFlow()
    }
}