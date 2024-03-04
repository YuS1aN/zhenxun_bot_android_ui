package me.kbai.zhenxunui.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentConversationBinding
import me.kbai.zhenxunui.extends.launchAndCollectIn
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.viewmodel.ConversationViewModel

class ConversationFragment : BaseFragment<FragmentConversationBinding>() {

    companion object {
        const val ARGS_GROUP_ID = "GROUP_ID"
        const val ARGS_USER_ID = "USER_ID"
        const val ARGS_NAME = "NAME"
    }

    private val mViewModel by viewModels<ConversationViewModel>({ requireActivity() })

    private val mMessageAdapter = MessageAdapter()

    private var mGroupId: String? = null
    private var mUserId: String? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentConversationBinding.inflate(inflater)

    override fun initView(): Unit = viewBinding.run {
        rvMessage.adapter = mMessageAdapter

        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            arguments?.getString(ARGS_NAME)

        etText.addTextChangedListener {
            btnSend.isEnabled = !it.isNullOrEmpty()
        }

        btnSend.setOnDebounceClickListener {
            val message = etText.text?.toString()
            if (message.isNullOrEmpty()) {
                return@setOnDebounceClickListener
            }
            etText.setText("")

            mViewModel.sendMessage(mUserId, mGroupId, message)
                .launchAndCollectIn(this@ConversationFragment) {
                    if (it.success()) {
                        mMessageAdapter.addData(
                            mViewModel.insertSentMessage(mGroupId, mUserId, message)
                        )
                    } else {
                        GlobalToast.showToast(it.message)
                    }
                }
        }
    }

    override fun initData() {
        mGroupId = arguments?.getString(ARGS_GROUP_ID)
        mUserId = arguments?.getString(ARGS_USER_ID)

        val groupId = mGroupId
        val userId = mUserId

        if (!groupId.isNullOrBlank()) {
            mMessageAdapter.setData(mViewModel.getGroupHistory(groupId))
            mViewModel.getGroupConversation(groupId).observe(this) {
                mMessageAdapter.addData(it)
            }
        } else if (!userId.isNullOrBlank()) {
            mMessageAdapter.setData(mViewModel.getFriendHistory(userId))
            mViewModel.getUserConversation(userId).observe(this) {
                mMessageAdapter.addData(it)
            }
        }
    }
}