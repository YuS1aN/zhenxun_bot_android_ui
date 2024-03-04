package me.kbai.zhenxunui.ui.group

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.webkit.WebViewAssetLoader
import com.google.gson.JsonArray
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentEditGroupBinding
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.extends.dp
import me.kbai.zhenxunui.extends.isNightMode
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
class EditGroupFragment : BaseFragment<FragmentEditGroupBinding>() {

    companion object {
        const val ARGS_GROUP_ID = "GROUP_ID"
    }

    private lateinit var mGroupId: String

    private val mViewModel by viewModels<EditGroupViewModel>()

    private val mAssetLoader by lazy {
        WebViewAssetLoader.Builder()
            .setDomain(ConsoleFragment.LOCAL_DOMAIN)
            .setHttpAllowed(true)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .build()
    }

    private lateinit var mFavouriteWebViewClient: ConsoleChartWebViewClient

    private var mPassiveAdapter: PassiveTasksPagerAdapter? = null

    private var mPluginStatusAdapter: PluginStatusAdapter? = null

    private var mSaving = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditGroupBinding.inflate(inflater)

    override fun initView(): Unit = viewBinding.run {
        sbGroupLevel.setOnProgressChangedListener { progress, _ ->
            etGroupLevel.setText(progress.toString())
        }
        mFavouriteWebViewClient =
            icFavouritePlugins.wvCharts.initChartWebView(ConsoleFragment.BAR_CHART_FILE)
    }

    override fun initData() {
        mGroupId = arguments?.getString(ARGS_GROUP_ID)!!

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

    private fun setPluginStatus(group: GroupInfo?, list: List<PluginInfo>?) {
        if (group == null) return
        if (list == null) return

        viewBinding.rvPlugin.adapter =
            PluginStatusAdapter(list, group).also { mPluginStatusAdapter = it }
    }

    private fun addSaveButton() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.group_info_menu_items, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.btn_save) {
                    updateGroup()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.icFavouritePlugins.wvCharts.destroy()
    }

    private fun updateGroup() = viewLifecycleScope.launch {
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
        icFavouritePlugins.apply {
            tvTitle.text = getString(R.string.favourite_plugins)
            viewLifecycleScope.launch {
                mFavouriteWebViewClient.blockDuringLoading {
                    wvCharts.setChartData(data.favouritePlugins)
                }
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

    private fun getSpannedCount(@StringRes id: Int, count: Int) =
        SpannableString(getString(id, count)).apply {
            val colorSpan = ForegroundColorSpan(
                ResourcesCompat.getColor(resources, R.color.text_gray, null)
            )
            setSpan(colorSpan, 0, indexOf(':') + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.initChartWebView(path: String): ConsoleChartWebViewClient {
        settings.run {
            javaScriptEnabled = true
            setSupportZoom(false)
            setNeedInitialFocus(false)
            builtInZoomControls = true
            loadWithOverviewMode = true
            useWideViewPort = true
            loadsImagesAutomatically = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            setSupportMultipleWindows(true)
        }
        val client = ConsoleChartWebViewClient(mAssetLoader) { webView, _ ->
            webView.setChartNightMode(resources.configuration.isNightMode())
        }
        webViewClient = client
        setBackgroundColor(0)
        loadUrl(path)
        return client
    }

    private fun WebView.setChartNightMode(isNightMode: Boolean) {
        evaluateJavascript("javascript:setDarkMode($isNightMode)") {}
    }

    private fun WebView.setChartData(data: Map<String, Int>) {
        val array = JsonArray()
        data.forEach {
            val obj = JsonArray()
            obj.add(it.key)
            obj.add(it.value)
            array.add(obj)
        }
        evaluateJavascript("javascript:setChartData2($array)", null)
    }
}