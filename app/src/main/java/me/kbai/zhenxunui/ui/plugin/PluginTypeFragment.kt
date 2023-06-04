package me.kbai.zhenxunui.ui.plugin

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentPluginTypeBinding
import me.kbai.zhenxunui.ext.apiCollect
import me.kbai.zhenxunui.ext.dp
import me.kbai.zhenxunui.ext.setOnDebounceClickListener
import me.kbai.zhenxunui.ext.viewLifecycleScope
import me.kbai.zhenxunui.model.PluginData
import me.kbai.zhenxunui.model.PluginType
import me.kbai.zhenxunui.model.UpdatePlugin
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

    private lateinit var mType: PluginType

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPluginTypeBinding.inflate(inflater)

    override fun initView() {
        val onConfirmListener =
            { dialog: Dialog, button: View, position: Int, update: UpdatePlugin ->
                viewLifecycleScope.launch {
                    mViewModel.updatePlugin(update)
                        .apiCollect(button) {
                            if (it.status == Resource.Status.LOADING) return@apiCollect
                            dialog.dismiss()
                            syncPluginData(position, mAdapter.data[position], update)
                        }
                }
                Unit
            }

        mAdapter.onItemEditClickListener = {
            EditPluginDialog(it, mAdapter.data[it], requireContext(), onConfirmListener).show()
        }

        mAdapter.onItemSwitchClickListener = { position, switch, isChecked ->
            viewLifecycleScope.launch {
                val plugin = mAdapter.data[position]
                val update = plugin.makeUpdatePlugin(blockType = if (isChecked) "" else "全部")

                mViewModel.updatePlugin(update)
                    .apiCollect(switch) {
                        if (it.status == Resource.Status.LOADING) return@apiCollect
                        if (!it.success()) switch.isChecked = !switch.isChecked
                        syncPluginData(position, plugin, update)
                    }
            }
        }

        viewBinding.rvPlugin.run {
            adapter = mAdapter
            addItemDecoration(CommonDecoration(12.dp.toInt()))
        }

        viewBinding.icError.btnRetry.setOnDebounceClickListener { requestPlugins() }
    }

    private fun updateLocalPluginData(position: Int, plugin: PluginData) {
        mViewModel.modifyPluginData(position, plugin, false)
        mAdapter.notifyItemChanged(position)
    }

    private suspend fun syncPluginData(position: Int, plugin: PluginData, update: UpdatePlugin) {
        //先展示本地修改后的数据
        updateLocalPluginData(position, plugin.applyUpdatePlugin(update))
        //再向服务端同步一次插件数据
        val result = mViewModel.requestPlugin(mType, plugin.module).apiCollect() ?: return
        if (result.success() && result.data != null) {
            updateLocalPluginData(position, result.data)
        }
    }

    override fun initData() {
        @Suppress("DEPRECATION")
        mType = arguments?.getSerializable(ARGS_TYPE) as PluginType

        if (mViewModel.plugins.value.isEmpty()) requestPlugins()

        viewLifecycleScope.launch {
            mViewModel.plugins.collect { mAdapter.data = it }
        }
    }

    private fun requestPlugins() = viewLifecycleScope.launch {
        viewBinding.icError.root.isVisible = false

        mViewModel.requestPlugins(mType).apiCollect {
            if (it.status == Resource.Status.LOADING) {
                viewBinding.icLoading.root.isVisible = true
                return@apiCollect
            }
            viewBinding.icLoading.root.isVisible = false
            if (!it.success()) {
                GlobalToast.showToast(it.message)
                viewBinding.icError.root.isVisible = true
            }
        }
    }
}