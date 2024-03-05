package me.kbai.zhenxunui.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentRequestTypeBinding
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.model.HandleRequest
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.ui.common.PromptDialogFragment
import me.kbai.zhenxunui.viewmodel.RequestViewModel
import me.kbai.zhenxunui.widget.CommonDecoration

/**
 * @author Sean on 2023/6/8
 */
class RequestTypeFragment : BaseFragment<FragmentRequestTypeBinding>() {
    companion object {
        private const val ARGS_TYPE = "type"

        fun newInstance(type: String): RequestTypeFragment {
            val args = Bundle()
            args.putSerializable(ARGS_TYPE, type)
            val fragment = RequestTypeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mType by lazy { requireArguments().getString(ARGS_TYPE)!! }
    private val mViewModel by viewModels<RequestViewModel>({ requireParentFragment() })
    private val mListAdapter = RequestListAdapter()

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentRequestTypeBinding = FragmentRequestTypeBinding.inflate(inflater, container, false)

    override fun initView(): Unit = viewBinding.run {
        rvRequest.adapter = mListAdapter
        rvRequest.addItemDecoration(CommonDecoration(resources.getDimensionPixelSize(R.dimen.list_item_margin)))

        root.setOnRefreshListener { getRequest(true) }
        icError.btnRetry.setOnDebounceClickListener { getRequest() }

        mListAdapter.onItemClickListener = { handle, request ->
            val context = root.context
            PromptDialogFragment()
                .setText(
                    context.getString(
                        R.string.confirm_handle_request,
                        when (handle) {
                            "approve" -> context.getString(R.string.approve)
                            "refuse" -> context.getString(R.string.refuse)
                            "delete" -> context.getString(R.string.delete)
                            else -> ""
                        },
                        request.botId
                    )
                )
                .setOnConfirmClickListener { dialog ->
                    dialog.dismiss()
                    viewLifecycleScope.launch {
                        mListAdapter.setItemEnabled(request.botId, false)

                        mViewModel.handleRequest(
                            handle,
                            HandleRequest(request.botId, request.flag, mType)
                        )
                            .filter { it.status != Resource.Status.LOADING }
                            .apiCollect {
                                mListAdapter.setItemEnabled(request.botId, true)
                                GlobalToast.showToast(it.message)
                                getRequest(true)
                            }
                    }
                }
                .show(childFragmentManager)
        }
    }

    override fun initData() {
        getRequest()
    }

    private fun getRequest(refresh: Boolean = false) = viewLifecycleScope.launch {
        val latestData = when (mType) {
            "friend" -> mViewModel.friendRequests
            "group" -> mViewModel.groupRequests
            else -> return@launch
        }
        if (!refresh && latestData != null) {
            mListAdapter.data = latestData
            if (latestData.isEmpty()) {
                showNoDataPage()
            }
            return@launch
        }

        if (refresh) viewBinding.root.isRefreshing = true
        viewBinding.icError.root.isVisible = false

        mViewModel.getRequest().apiCollect {
            if (it.status == Resource.Status.LOADING) {
                if (!refresh) viewBinding.icLoading.root.isVisible = true
                return@apiCollect
            }
            viewBinding.root.isRefreshing = false
            viewBinding.icLoading.root.isVisible = false

            if (!it.success() || it.data == null) {
                showErrorPage()
                GlobalToast.showToast(it.message)
                return@apiCollect
            }

            val data = when (mType) {
                "friend" -> it.data.friend
                "group" -> it.data.group
                else -> return@apiCollect
            }
            if (data.isEmpty()) {
                showNoDataPage()
                return@apiCollect
            }
            mListAdapter.data = data
        }
    }

    private fun showNoDataPage() = viewBinding.icError.run {
        root.isVisible = true
        GlideApp.with(ivImage)
            .load(R.drawable.ic_no_data)
            .into(ivImage)
        tvText.setText(R.string.no_requests)
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

    suspend fun clearRequest() {
        val result = mViewModel.clearRequest(mType).apiCollect()
        GlobalToast.showToast(result.message)
        getRequest(true)
    }
}