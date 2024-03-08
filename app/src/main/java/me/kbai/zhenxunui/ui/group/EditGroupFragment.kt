package me.kbai.zhenxunui.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.FragmentEditGroupBinding
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.extends.dp
import me.kbai.zhenxunui.extends.setOnProgressChangedListener
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.PluginInfo
import me.kbai.zhenxunui.model.UpdateGroup
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.ui.console.ConsoleChartWebViewClient
import me.kbai.zhenxunui.ui.console.ConsoleFragment
import me.kbai.zhenxunui.viewmodel.EditGroupViewModel

/**
 * @author Sean on 2023/6/7
 */
class EditGroupFragment : BaseEditInfoFragment<FragmentEditGroupBinding>() {

    companion object {
        const val ARGS_GROUP_ID = "GROUP_ID"
    }

    private lateinit var mGroupId: String

    private val mViewModel by viewModels<EditGroupViewModel>()

    private lateinit var mFavouriteWebViewClient: ConsoleChartWebViewClient

    private var mPassiveAdapter: PassiveTasksPagerAdapter? = null

    private var mPluginStatusAdapter: PluginStatusAdapter? = null

    private var mSaving = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditGroupBinding.inflate(inflater, container, false)

    override fun initView(): Unit = viewBinding.run {
        sbGroupLevel.setOnProgressChangedListener { progress, _ ->
            etGroupLevel.setText(progress.toString())
        }
        icFavouritePlugins.apply {
            tvTitle.text = getString(R.string.favourite_plugins)
            mFavouriteWebViewClient = wvCharts.initChartWebView(ConsoleFragment.BAR_CHART_FILE)
        }
    }

    override fun initData() {
        mGroupId = arguments?.getString(ARGS_GROUP_ID)!!

        mViewModel.message.observe(viewLifecycleOwner) {
            GlobalToast.showToast(it)
        }

        mViewModel.groupInfo.observe(viewLifecycleOwner) {
            viewBinding.bindGroupInfoData(it)
        }
        mViewModel.plugins.observe(viewLifecycleOwner) { list ->
            val group = mViewModel.groupInfo.value ?: return@observe
            setPluginStatus(group, list)
        }

        mViewModel.requestGroupInfo(mGroupId)

        addSaveButton()
    }

    override fun getWebViews() = arrayOf(viewBinding.icFavouritePlugins.wvCharts)

    private fun setPluginStatus(group: GroupInfo?, list: List<PluginInfo>?) {
        if (group == null) return
        if (list == null) return

        viewBinding.rvPlugin.adapter =
            PluginStatusAdapter(list, group).also { mPluginStatusAdapter = it }
    }

    override fun updateInfo() = viewLifecycleScope.launch {
        if (mSaving) return@launch
        mSaving = true
        val updateGroup = collectUpdateData()
        if (updateGroup == null) {
            mSaving = false
            return@launch
        }
        mViewModel.updateGroup(updateGroup)
            .apiCollect {
                if (it.status == Resource.Status.LOADING) return@apiCollect
                GlobalToast.showToast(it.message)
            }
        mSaving = false
    }

    private fun collectUpdateData(): UpdateGroup? = viewBinding.run {
        val closedPlugins = mPluginStatusAdapter?.getClosedPlugins()
        val enabledTasks = mPassiveAdapter?.getEnabledTasks()

        if (closedPlugins == null || enabledTasks == null) {
            return@run null
        }

        UpdateGroup(
            mGroupId,
            swEnabled.isChecked,
            sbGroupLevel.progress,
            closedPlugins,
            enabledTasks
        )
    }

    private fun FragmentEditGroupBinding.bindGroupInfoData(data: GroupInfo) {
        GlideApp.with(ivAvatar)
            .load(data.avatarUrl)
            .into(ivAvatar)
        tvName.text = data.name
        tvId.text = data.groupId
        tvNum.text = getSpannedCount(R.string.number_format, data.memberCount)
        tvMax.text = getSpannedCount(R.string.max_format, data.maxMemberCount)
        tvReceived.text = getSpannedCount(R.string.received_format, data.chatCount)
        tvCall.text = getSpannedCount(R.string.fn_call_format, data.callCount)
        swEnabled.isChecked = data.status
        viewLifecycleScope.launch {
            mFavouriteWebViewClient.blockDuringLoading {
                icFavouritePlugins.wvCharts.setChartData(data.favouritePlugins)
            }
        }
        sbGroupLevel.progress = data.level
        vpPassive.apply {
            offscreenPageLimit = 2
            setPageTransformer(false) { page, position ->
                page.apply {
                    translationX = -position * 40.dp
                }
            }
            adapter = PassiveTasksPagerAdapter(data.task).also { mPassiveAdapter = it }
        }
    }
}