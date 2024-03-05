package me.kbai.zhenxunui.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.FragmentEditUserBinding
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.model.UserDetail
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.ui.console.ConsoleChartWebViewClient
import me.kbai.zhenxunui.ui.console.ConsoleFragment
import me.kbai.zhenxunui.viewmodel.EditUserViewModel

class EditUserFragment : BaseEditInfoFragment<FragmentEditUserBinding>() {
    companion object {
        const val ARGS_USER_ID = "USER_ID"
    }

    private val mViewModel by viewModels<EditUserViewModel>()

    private lateinit var mUserId: String

    private lateinit var mFavouriteWebViewClient: ConsoleChartWebViewClient

    override fun getWebViews() = arrayOf(viewBinding.icFavouritePlugins.wvCharts)
    override fun updateInfo() {
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditUserBinding.inflate(inflater, container, false)

    override fun initView(): Unit = viewBinding.run {
        icFavouritePlugins.apply {
            tvTitle.text = getString(R.string.favourite_plugins)
            mFavouriteWebViewClient = wvCharts.initChartWebView(ConsoleFragment.BAR_CHART_FILE)
        }
    }

    override fun initData() {
        mUserId = arguments?.getString(ARGS_USER_ID)!!

        mViewModel.userDetail.observe(viewLifecycleOwner) {
            if (it.success() && it.data != null) {
                viewBinding.bindData(it.data)
            } else {
                GlobalToast.showToast(it.message)
            }
        }

        mViewModel.requestPluginDetail(mUserId)

        //addSaveButton()
    }

    private fun FragmentEditUserBinding.bindData(data: UserDetail) {
        tvName.text = if (data.remark.isNotEmpty()) {
            "${data.remark}(${data.nickname})"
        } else {
            data.nickname
        }
        tvId.text = data.userId
        tvReceived.text = getSpannedCount(R.string.received_format, data.chatCount)
        tvCall.text = getSpannedCount(R.string.fn_call_format, data.callCount)
        swBanned.isChecked = data.banned
        viewLifecycleScope.launch {
            mFavouriteWebViewClient.blockDuringLoading {
                icFavouritePlugins.wvCharts.setChartData(data.favouritePlugins)
            }
        }
    }
}