package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.FriendListItem
import me.kbai.zhenxunui.model.GroupListItem
import me.kbai.zhenxunui.repository.ApiRepository
import me.kbai.zhenxunui.repository.Resource
import java.security.InvalidParameterException

/**
 * @author Sean on 2023/6/7
 */
class FriendListViewModel : ViewModel() {
    var botId: String? = null

    private val _friends: MutableStateFlow<List<FriendListItem>> = MutableStateFlow(emptyList())
    val friends: StateFlow<List<FriendListItem>> = _friends

    private val _groups: MutableStateFlow<List<GroupListItem>> = MutableStateFlow(emptyList())
    val groups: StateFlow<List<GroupListItem>> = _groups

    suspend fun requestList(type: Int): Resource<*> = coroutineScope {
        val botId = Constants.currentBot?.selfId
        this@FriendListViewModel.botId = botId

        var friendsReq: Deferred<Resource<*>>? = null
        var groupsReq: Deferred<Resource<*>>? = null

        if (botId.isNullOrBlank()) {
            return@coroutineScope Resource.error(null, "No bot selected.", -1)
        }

        if ((type and FriendListType.FRIEND) == FriendListType.FRIEND) {
            friendsReq = async {
                val friendsRes = ApiRepository.getFriendList(botId).apiCollect()
                if (friendsRes.success() && friendsRes.data != null) {
                    _friends.update { friendsRes.data }
                }
                friendsRes
            }
        }
        if ((type and FriendListType.GROUP) == FriendListType.GROUP) {
            groupsReq = async {
                val groupsRes = ApiRepository.getGroupList(botId).apiCollect()
                if (groupsRes.success() && groupsRes.data != null) {
                    _groups.update { groupsRes.data }
                }
                groupsRes
            }
        }
        val friendsResult = friendsReq?.await()
        if (friendsResult?.status == Resource.Status.FAIL) {
            return@coroutineScope friendsResult
        }
        val groupsResult = groupsReq?.await()
        if (groupsResult?.status == Resource.Status.FAIL) {
            return@coroutineScope groupsResult
        }
        return@coroutineScope friendsResult ?: groupsResult
        ?: throw InvalidParameterException("Wrong type.")
    }
}

interface FriendListType {
    companion object {
        const val FRIEND = 1
        const val GROUP = 2
    }
}