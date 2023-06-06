package me.kbai.zhenxunui.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentGroupBinding
import me.kbai.zhenxunui.ext.apiCollect
import me.kbai.zhenxunui.ext.setOnDebounceClickListener
import me.kbai.zhenxunui.ext.viewLifecycleScope
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.viewmodel.GroupViewModel
import me.kbai.zhenxunui.widget.CommonDecoration

/**
 * @author Sean on 2023/5/30
 */
class GroupFragment : BaseFragment<FragmentGroupBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentGroupBinding = FragmentGroupBinding.inflate(inflater)

    private val mViewModel by viewModels<GroupViewModel>()
    private val mAdapter = GroupListAdapter()

    override fun initView(): Unit = viewBinding.run {
        rvGroup.adapter = mAdapter
        rvGroup.addItemDecoration(CommonDecoration(resources.getDimensionPixelSize(R.dimen.list_item_margin)))
        mAdapter.onItemClickListener = { position ->
            EditGroupDialog(root.context, mAdapter.data[position]) { dialog, button, update ->
                viewLifecycleScope.launch {
                    val result = mViewModel.updateGroup(update).apiCollect(button)
                    GlobalToast.showToast(result.message)
                    dialog.dismiss()
                    requestGroupData(true)
                }
            }.show()
        }
        root.setOnRefreshListener { requestGroupData(true) }
        icError.btnRetry.setOnDebounceClickListener { requestGroupData() }
    }

    override fun initData() {
        requestGroupData()
    }

    private fun requestGroupData(refresh: Boolean = false) = viewLifecycleScope.launch {
        val latestData = mViewModel.groupInfo
        if (!refresh && latestData != null) {
            mAdapter.data = latestData
            if (latestData.isEmpty()) {
                showNoDataPage()
            }
            return@launch
        }

        if (refresh) {
            viewBinding.root.isRefreshing = true
        }

        viewBinding.icError.root.isVisible = false

        mViewModel.getGroup()
            .filter { it.status != Resource.Status.LOADING }
            .apiCollect {
                viewBinding.root.isRefreshing = false

                if (!it.success()) {
                    showErrorPage()
                    return@apiCollect
                }
                if (it.data.isNullOrEmpty()) {
                    showNoDataPage()
                    return@apiCollect
                }
                mAdapter.data = it.data
            }
    }

    private fun showNoDataPage() = viewBinding.icError.run {
        root.isVisible = true
        GlideApp.with(ivImage)
            .load(R.drawable.ic_no_data)
            .into(ivImage)
        tvText.setText(R.string.error_no_data)
        btnRetry.isVisible = false
    }

    private fun showErrorPage() = viewBinding.icError.run {
        root.isVisible = true
        GlideApp.with(ivImage)
            .load(R.drawable.ic_error)
            .into(ivImage)
        tvText.text = null
        btnRetry.isVisible = true
    }
}