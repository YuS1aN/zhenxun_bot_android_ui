package me.kbai.zhenxunui.ui.request

import android.view.LayoutInflater
import android.view.ViewGroup
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentRequestBinding

/**
 * @author Sean on 2023/5/30
 */
class RequestFragment : BaseFragment<FragmentRequestBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRequestBinding = FragmentRequestBinding.inflate(inflater)

    override fun initView() {
    }
}