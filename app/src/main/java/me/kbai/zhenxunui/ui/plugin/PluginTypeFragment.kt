package me.kbai.zhenxunui.ui.plugin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentPluginTypeBinding
import me.kbai.zhenxunui.ext.apiCollect
import me.kbai.zhenxunui.ext.dp
import me.kbai.zhenxunui.ext.setOnDebounceClickListener
import me.kbai.zhenxunui.ext.viewLifecycleScope
import me.kbai.zhenxunui.model.PluginType
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.viewmodel.PluginTypeViewModel
import me.kbai.zhenxunui.widget.CommonDecoration

/**
 * @author Sean on 2023/6/2
 */
class PluginTypeFragment : BaseFragment<FragmentPluginTypeBinding>() {

    companion object {
        private const val ARGS_TYPE = "type"

        fun newInstance(type: PluginType): PluginTypeFragment {
            val args = Bundle()
            args.putSerializable(ARGS_TYPE, type)
            val fragment = PluginTypeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mViewModel by viewModels<PluginTypeViewModel>()

    private val mAdapter = PluginAdapter()

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPluginTypeBinding.inflate(inflater)

    override fun initView() {
        viewBinding.rvPlugin.run {
            adapter = mAdapter
            addItemDecoration(CommonDecoration(12.dp.toInt()))
        }

        viewBinding.icError.btnRetry.setOnDebounceClickListener { requestPlugins() }
    }

    override fun initData() {
        requestPlugins()
    }

    private fun requestPlugins() = viewLifecycleScope.launch {
        viewBinding.icError.root.isVisible = false
        @Suppress("DEPRECATION")
        val type = arguments?.getSerializable(ARGS_TYPE) as PluginType
        mViewModel.requestPlugins(type)
            .apiCollect {
                if (it.status == Resource.Status.LOADING) {
                    viewBinding.icLoading.root.isVisible = true
                    return@apiCollect
                }
                viewBinding.icLoading.root.isVisible = false

                if (it.success()) {
                    mAdapter.data = it.data!!
                } else {
                    GlobalToast.showToast(it.message)
                    viewBinding.icError.root.isVisible = true
                }
            }
    }
}