package me.kbai.zhenxunui.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentHomeBinding

/**
 * @author Sean on 2023/5/30
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater)

    override fun initView() {
    }
}