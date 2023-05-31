package me.kbai.zhenxunui.ui.console

import android.view.LayoutInflater
import android.view.ViewGroup
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentConsoleBinding

/**
 * @author Sean on 2023/5/30
 */
class ConsoleFragment : BaseFragment<FragmentConsoleBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConsoleBinding = FragmentConsoleBinding.inflate(inflater)

    override fun initView() {
    }
}