package me.kbai.zhenxunui.ui.plugin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentPluginTypeBinding
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.extends.launchAndCollectIn
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.model.PluginType
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.tool.glide.GlideApp
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
        viewBinding.icError.btnRetry.isVisible = true
        GlideApp.with(this)
            .load(R.drawable.ic_error)
            .into(viewBinding.icError.ivImage)

//        val onEditPluginListener =
//            { dialog: Dialog, button: View, position: Int, update: UpdatePlugin ->
//                viewLifecycleScope.launch {
//                    mViewModel.updatePlugin(update)
//                        .apiCollect(button) {
//                            if (it.status == Resource.Status.LOADING) return@apiCollect
//                            if (it.status == Resource.Status.FAIL) GlobalToast.showToast(it.message)
//                            dialog.dismiss()
//                            syncPluginData(position, mAdapter.data[position], update)
//                        }
//                }
//                Unit
//            }
//
        mAdapter.onItemEditClickListener = {
            EditPluginDialogFragment(mAdapter.data[it]) {
                //TODO
            }.show(childFragmentManager)
        }
//
//        val onEditConfigListener =
//            { dialog: Dialog, button: View, position: Int, updateConfigs: List<UpdateConfig> ->
//                viewLifecycleScope.launch {
//                    mViewModel.updateConfig(updateConfigs)
//                        .apiCollect(button) {
//                            if (it.status == Resource.Status.LOADING) return@apiCollect
//                            if (it.status == Resource.Status.FAIL) GlobalToast.showToast(it.message)
//                            dialog.dismiss()
//                            syncPluginData(position, mAdapter.data[position], updateConfigs)
//                        }
//                }
//                Unit
//            }

//        mAdapter.onItemConfigClickListener = {
//            EditConfigDialog(
//                it,
//                mAdapter.data[it].config!!,
//                requireContext(),
//                onEditConfigListener
//            ).show()
//        }

//        mAdapter.onItemSwitchClickListener = { position, isChecked ->
//            viewLifecycleScope.launch {
//                val plugin = mAdapter.data[position]
//                val update = plugin.makeUpdatePlugin(blockType = if (isChecked) "" else "全部")
//
//                val holder = mAdapter.holderMap[plugin.module]!!
//                holder.binding.run {
//                    root.tag = plugin.module
//                    holder.setEnabled(false)
//                    mAdapter.nonEditableSet.add(plugin.module)
//                }
//
//                mViewModel.updatePlugin(update)
//                    .apiCollect {
//                        if (it.status == Resource.Status.LOADING) return@apiCollect
//                        mAdapter.nonEditableSet.remove(plugin.module)
//                        mAdapter.holderMap[plugin.module]?.run {
//                            if (module == plugin.module) setEnabled(true)
//                        }
//                        if (!it.success()) {
//                            update.blockType = if (plugin.manager.status) "" else "全部"
//                        }
//                        syncPluginData(position, plugin, update)
//                    }
//            }
//        }

        viewBinding.rvPlugin.run {
            adapter = mAdapter
            addItemDecoration(CommonDecoration(resources.getDimensionPixelSize(R.dimen.list_item_margin)))
            itemAnimator = object : DefaultItemAnimator() {

                override fun animateChange(
                    oldHolder: RecyclerView.ViewHolder?,
                    newHolder: RecyclerView.ViewHolder?,
                    fromX: Int,
                    fromY: Int,
                    toX: Int,
                    toY: Int
                ): Boolean {
                    val su = super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY)
                    newHolder?.run {
                        itemView.translationX = 0f
                        itemView.translationY = 0f
                        itemView.alpha = 1f
                    }
                    return su
                }
            }
        }

        viewBinding.icError.btnRetry.setOnDebounceClickListener { requestPlugins() }
    }

//    private fun updateLocalPluginData(position: Int, plugin: PluginInfo) {
//        mViewModel.modifyPluginData(position, plugin, false)
//        mAdapter.notifyItemChanged(position)
//    }
//
//    private suspend fun syncByRemoteData(position: Int, plugin: PluginInfo) {
//        val result = mViewModel.requestPlugin(mType, plugin.module).apiCollect()
//        if (result.success() && result.data != null) {
//            updateLocalPluginData(position, result.data)
//        }
//    }

//    private suspend fun syncPluginData(position: Int, plugin: PluginInfo, update: UpdatePlugin) {
//        //先展示本地修改后的数据
//        updateLocalPluginData(position, plugin.applyUpdatePlugin(update))
//        //再向服务端同步一次插件数据
//        syncByRemoteData(position, plugin)
//    }

//    private suspend fun syncPluginData(
//        position: Int,
//        plugin: PluginInfo,
//        updateConfigs: List<UpdateConfig>
//    ) {
//        //先展示本地修改后的数据
//        updateLocalPluginData(position, plugin.applyUpdateConfig(updateConfigs))
//        //再向服务端同步一次插件数据
//        syncByRemoteData(position, plugin)
//    }

    override fun initData() {
        @Suppress("DEPRECATION")
        mType = arguments?.getSerializable(ARGS_TYPE) as PluginType

        mViewModel.plugins.launchAndCollectIn(this) { mAdapter.data = it }
        if (mViewModel.plugins.value.isEmpty()) requestPlugins()
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