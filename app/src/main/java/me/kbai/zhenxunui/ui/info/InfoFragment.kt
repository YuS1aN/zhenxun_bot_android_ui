package me.kbai.zhenxunui.ui.info

import android.view.LayoutInflater
import android.view.ViewGroup
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentInfoBinding

/**
 * @author Sean on 2023/5/30
 */
class InfoFragment : BaseFragment<FragmentInfoBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoBinding = FragmentInfoBinding.inflate(inflater)

    override fun initView() {
    }
}