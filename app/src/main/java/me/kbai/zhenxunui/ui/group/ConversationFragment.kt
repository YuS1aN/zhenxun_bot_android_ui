package me.kbai.zhenxunui.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentConversationBinding

class ConversationFragment : BaseFragment<FragmentConversationBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentConversationBinding.inflate(inflater)

    override fun initView() {

    }
}