package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.onEach
import me.kbai.zhenxunui.model.HandleRequest
import me.kbai.zhenxunui.model.RequestResult
import me.kbai.zhenxunui.repository.ApiRepository

/**
 * @author Sean on 2023/6/8
 */
class RequestTypeViewModel : ViewModel() {
    var requests: List<RequestResult>? = null

    fun getRequest(type: String) = ApiRepository.getRequest(type).onEach {
        if (it.success() && it.data != null) requests = it.data
    }

    fun clearRequest(type: String) = ApiRepository.clearRequest(type)

    fun handleRequest(handle: HandleRequest) = ApiRepository.handleRequest(handle)
}