package me.kbai.zhenxunui.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentGroupBinding

/**
 * @author Sean on 2023/5/30
 */
class GroupFragment : BaseFragment<FragmentGroupBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGroupBinding = FragmentGroupBinding.inflate(inflater)

    override fun initView() {
    }
}