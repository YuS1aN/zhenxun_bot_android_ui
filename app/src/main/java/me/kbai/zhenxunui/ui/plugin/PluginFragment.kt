package me.kbai.zhenxunui.ui.plugin

import android.view.LayoutInflater
import android.view.ViewGroup
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentPluginBinding

/**
 * @author Sean on 2023/5/30
 */
class PluginFragment : BaseFragment<FragmentPluginBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPluginBinding = FragmentPluginBinding.inflate(inflater)

    override fun initView() {
    }
}