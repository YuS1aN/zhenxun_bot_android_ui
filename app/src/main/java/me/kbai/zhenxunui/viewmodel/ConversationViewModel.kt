package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.extends.logE
import me.kbai.zhenxunui.model.ChatMessage
import me.kbai.zhenxunui.model.MessageType
import me.kbai.zhenxunui.model.SendMessage
import me.kbai.zhenxunui.repository.ApiRepository

/**
 * lifecycle scope: activity
 */
class ConversationViewModel : ViewModel() {

    private val _groupHistories = HashMap<String, MutableList<ChatMessage>>()
    private val _friendHistories = HashMap<String, MutableList<ChatMessage>>()
    private val _currentMessage: MutableLiveData<ChatMessage> = MutableLiveData()

    private val mChatWebSocketHolder =
        ApiRepository.newChatWebSocket(viewModelScope) { message, exception ->
            if (message == null) {
                logE(exception!!)
                return@newChatWebSocket
            }
            getConversationInner(message).add(message)
            _currentMessage.value = message
        }

    private fun getConversationInner(obj: ChatMessage): MutableList<ChatMessage> =
        if (obj.groupId.isNullOrBlank()) {
            _friendHistories[obj.userId]
                ?: ArrayList<ChatMessage>().also {
                    _friendHistories[obj.userId] = it
                }
        } else {
            _groupHistories[obj.groupId]
                ?: ArrayList<ChatMessage>().also {
                    _groupHistories[obj.groupId] = it
                }
        }

    fun getGroupHistory(groupId: String): List<ChatMessage> =
        _groupHistories[groupId] ?: ArrayList<ChatMessage>().also {
            _groupHistories[groupId] = it
        }

    fun getFriendHistory(userId: String): List<ChatMessage> =
        _friendHistories[userId] ?: ArrayList<ChatMessage>().also {
            _friendHistories[userId] = it
        }

    fun getGroupConversation(groupId: String): LiveData<ChatMessage> {
        val liveData = MediatorLiveData<ChatMessage>()
        val observer = Observer<ChatMessage> {
            if (it.groupId == groupId) liveData.value = it
        }
        liveData.addSource(_currentMessage, observer)
        return liveData
    }

    fun getUserConversation(userId: String): LiveData<ChatMessage> {
        val liveData = MediatorLiveData<ChatMessage>()
        val observer = Observer<ChatMessage> {
            if (it.userId == userId && it.groupId.isNullOrBlank()) liveData.value = it
        }
        liveData.addSource(_currentMessage, observer)
        return liveData
    }

    fun openWebSocket() {
        mChatWebSocketHolder.connect()
    }

    fun closeWebSocket() {
        mChatWebSocketHolder.close()
    }

    fun sendMessage(userId: String?, groupId: String?, message: String) =
        ApiRepository.sendMessage(
            SendMessage(
                Constants.currentBot!!.selfId,
                userId,
                groupId,
                message
            )
        )

    fun insertSentMessage(groupId: String?, userId: String?, message: String): ChatMessage {
        val bot = Constants.currentBot!!
        val chatMessage = ChatMessage(
            "",
            bot.selfId,
            groupId,
            listOf(ChatMessage.MessageItem(MessageType.TEXT, message)),
            bot.nickname,
            bot.avatarUrl
        )
        if (groupId != null) getConversationInner(chatMessage).add(chatMessage)
        else if (userId != null) {
            val list = _friendHistories[userId]
                ?: ArrayList<ChatMessage>().also {
                    _friendHistories[userId] = it
                }
            list.add(chatMessage)
        }
        return chatMessage
    }

    override fun onCleared() {
        mChatWebSocketHolder.cancel()
    }
}