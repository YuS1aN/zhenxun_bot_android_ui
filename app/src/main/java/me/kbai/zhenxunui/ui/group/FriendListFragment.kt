package me.kbai.zhenxunui.ui.group

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentGroupBinding
import me.kbai.zhenxunui.extends.launchAndCollectIn
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.viewmodel.ConversationViewModel
import me.kbai.zhenxunui.viewmodel.FriendListType
import me.kbai.zhenxunui.viewmodel.FriendListViewModel

/**
 * @author Sean on 2023/5/30
 */
class FriendListFragment : BaseFragment<FragmentGroupBinding>() {
    companion object {
        const val SAVED_EXPAND_GROUP = "SAVED_EXPAND_GROUP"
        const val SAVED_EXPAND_FRIEND = "SAVED_EXPAND_FRIEND"
    }

    private val mViewModel by viewModels<FriendListViewModel>()
    private val mAdapter = FriendListAdapter()

    private val mConversationViewModel by viewModels<ConversationViewModel>({ requireActivity() })

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentGroupBinding = FragmentGroupBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mConversationViewModel.openWebSocket()
    }

    override fun initView(): Unit = viewBinding.run {
        rvFriends.adapter = mAdapter
        mAdapter.onGroupClickListener = {
            val args = Bundle().apply {
                putString(ConversationFragment.ARGS_GROUP_ID, it.groupId)
                putString(ConversationFragment.ARGS_NAME, it.groupName)
            }
            findNavController().navigate(R.id.action_nav_friend_list_to_conversationFragment, args)
        }
        mAdapter.onFriendClickListener = {
            val args = Bundle().apply {
                putString(ConversationFragment.ARGS_USER_ID, it.userId)
                putString(ConversationFragment.ARGS_NAME, it.nickname)
            }
            findNavController().navigate(R.id.action_nav_friend_list_to_conversationFragment, args)
        }
        root.setOnRefreshListener { requestData() }
        icError.btnRetry.setOnDebounceClickListener { requestData() }
    }

    override fun initData() {
        mViewModel.groups.launchAndCollectIn(this) {
            mAdapter.groupData = it
        }
        mViewModel.friends.launchAndCollectIn(this) {
            mAdapter.friendData = it
        }

        if (mViewModel.botId != Constants.currentBot?.selfId) {
            requestData()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.run {
            mAdapter.expandFriend = getBoolean(SAVED_EXPAND_FRIEND)
            mAdapter.expandGroup = getBoolean(SAVED_EXPAND_GROUP)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_EXPAND_FRIEND, mAdapter.expandFriend)
        outState.putBoolean(SAVED_EXPAND_GROUP, mAdapter.expandGroup)
    }

    private fun requestData(
        type: Int = FriendListType.FRIEND or FriendListType.GROUP
    ) = viewLifecycleScope.launch {
        viewBinding.root.isRefreshing = true
        viewBinding.icError.root.isVisible = false

        val result = mViewModel.requestList(type)

        viewBinding.root.isRefreshing = false
        if (!result.success()) {
            showErrorPage()
            return@launch
        }
        if (mViewModel.friends.value.isEmpty() && mViewModel.groups.value.isEmpty()) {
            showNoDataPage()
        }
    }

    private fun showNoDataPage() = viewBinding.icError.run {
        root.isVisible = true
        GlideApp.with(ivImage)
            .load(R.drawable.ic_no_data)
            .into(ivImage)
        tvText.setText(R.string.error_no_data)
        btnRetry.isVisible = false
    }

    private fun showErrorPage() = viewBinding.icError.run {
        root.isVisible = true
        GlideApp.with(ivImage)
            .load(R.drawable.ic_error)
            .into(ivImage)
        tvText.text = null
        btnRetry.isVisible = true
    }
}