package me.kbai.zhenxunui.ui.info

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentInfoBinding
import me.kbai.zhenxunui.viewmodel.InfoViewModel

/**
 * @author Sean on 2023/5/30
 */
class InfoFragment : BaseFragment<FragmentInfoBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoBinding = FragmentInfoBinding.inflate(inflater, container, false)

    private val mViewModel by viewModels<InfoViewModel>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {

    }

    override fun initData() {

    }
}