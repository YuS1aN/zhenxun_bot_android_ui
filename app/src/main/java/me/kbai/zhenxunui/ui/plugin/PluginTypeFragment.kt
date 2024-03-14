package me.kbai.zhenxunui.ui.plugin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentPluginTypeBinding
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.extends.launchAndCollectIn
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.model.PluginInfo
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

        private var mEditingPosition: Int = -1

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
    ) = FragmentPluginTypeBinding.inflate(inflater, container, false)

    override fun initView() {
        viewBinding.icError.btnRetry.isVisible = true
        GlideApp.with(this)
            .load(R.drawable.ic_error)
            .into(viewBinding.icError.ivImage)

        mAdapter.onItemEditClickListener = { position ->
            EditPluginDialogFragment.sPluginInfo = mAdapter.data[position]
            mEditingPosition = position
            EditPluginDialogFragment().show(childFragmentManager)
        }

        mAdapter.onItemSwitchClickListener = { position, button ->
            viewLifecycleScope.launch {

                mViewModel.pluginSwitch(mAdapter.data[position].module, button.isChecked)
                    .apiCollect {
                        if (it.status == Resource.Status.LOADING) return@apiCollect
                        GlobalToast.showToast(it.message)
                        delay(250)
                        syncPluginData(
                            position,
                            mAdapter.data[position].copy(status = button.isChecked),
                            button
                        )
                    }
            }
        }

        viewBinding.srlRefresh.setOnRefreshListener {
            requestPlugins()
        }

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

    private fun syncPluginData(position: Int, plugin: PluginInfo, button: View?) =
        viewLifecycleScope.launch {
            mViewModel.requestPlugin(plugin).apiCollect(button) {
                if (it.data == null) return@apiCollect
                mViewModel.modifyPluginData(position, it.data, false)
                mAdapter.notifyItemChanged(position)
            }
        }

    override fun initData() {
        @Suppress("DEPRECATION")
        mType = arguments?.getSerializable(ARGS_TYPE) as PluginType

        mViewModel.plugins.launchAndCollectIn(this) { mAdapter.data = it }

        EditPluginDialogFragment.updatePlugin.observe(viewLifecycleOwner) { consumable ->
            if (consumable.isConsumed || mEditingPosition < 0) return@observe
            val position = mEditingPosition

            consumable.get()?.let {
                val plugin = mAdapter.data[position]
                syncPluginData(position, it.applyToPluginInfo(plugin), null)
            }
        }

        if (mViewModel.plugins.value.isEmpty()) requestPlugins()
    }

    private fun requestPlugins() = viewLifecycleScope.launch {
        viewBinding.icError.root.isVisible = false

        mViewModel.requestPlugins(mType).apiCollect {
            if (it.status == Resource.Status.LOADING) {
                viewBinding.icLoading.root.isVisible = true
                return@apiCollect
            }
            viewBinding.srlRefresh.isRefreshing = false
            viewBinding.icLoading.root.isVisible = false
            if (!it.success()) {
                GlobalToast.showToast(it.message)
                viewBinding.icError.root.isVisible = true
            }
        }
    }
}