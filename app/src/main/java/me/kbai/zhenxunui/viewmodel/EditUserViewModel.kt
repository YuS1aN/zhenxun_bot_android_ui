package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.UserDetail
import me.kbai.zhenxunui.repository.ApiRepository
import me.kbai.zhenxunui.repository.Resource

class EditUserViewModel : ViewModel() {
    private val _userDetail: MutableLiveData<Resource<UserDetail>> = MutableLiveData()
    val userDetail: LiveData<Resource<UserDetail>> = _userDetail

    fun requestPluginDetail(userId: String) = viewModelScope.launch {
        ApiRepository.getUserDetail(Constants.currentBot!!.selfId, userId)
            .apiCollect {
                _userDetail.value = it
            }
    }
}