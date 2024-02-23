package me.kbai.zhenxunui.ui.console

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentConsoleBinding
import me.kbai.zhenxunui.extends.detach
import me.kbai.zhenxunui.extends.isNightMode
import me.kbai.zhenxunui.extends.runWithNoReturn
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.viewmodel.ConsoleViewModel

/**
 * @author Sean on 2023/5/30
 */
class ConsoleFragment : BaseFragment<FragmentConsoleBinding>() {

    companion object {
        private const val BAR_CHART_FILE = "file:///android_asset/console_bar_charts.html"
        private const val TANGENTIAL_BAR_FILE =
            "file:///android_asset/console_horizontal_bar_charts.html"
    }

    private val mViewModel by viewModels<ConsoleViewModel>()

    private var mCountdownJob: Job? = null

    private lateinit var mMessageCountWebClient: ConsoleChartWebViewClient
    private lateinit var mActiveGroupWebClient: ConsoleChartWebViewClient
    private lateinit var mPopularPluginWebClient: ConsoleChartWebViewClient

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentConsoleBinding = FragmentConsoleBinding.inflate(inflater)

    override fun initView() = viewBinding.run {
        icMessageCount.run {
            tvTitle.setText(R.string.message_count)
            mMessageCountWebClient = wvCharts.initChartWebView(TANGENTIAL_BAR_FILE)
        }
        icActiveGroup.run {
            tvTitle.setText(R.string.active_group)
            mActiveGroupWebClient = wvCharts.initChartWebView(BAR_CHART_FILE)
        }
        icPopularPlugin.run {
            tvTitle.setText(R.string.popular_plugin)
            mPopularPluginWebClient = wvCharts.initChartWebView(BAR_CHART_FILE)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.initChartWebView(path: String): ConsoleChartWebViewClient {
        settings.run {
            javaScriptEnabled = true
            setSupportZoom(false)
            setNeedInitialFocus(false)
            allowFileAccess = true
            builtInZoomControls = true
            loadWithOverviewMode = true
            useWideViewPort = true
            loadsImagesAutomatically = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        val client = ConsoleChartWebViewClient { webView, _ ->
            webView.setChartNightMode(resources.configuration.isNightMode())
        }
        webViewClient = client
        setBackgroundColor(0)
        loadUrl(path)
        return client
    }

    override fun initData() = viewBinding.runWithNoReturn {
        icSystemStatus.run {
            mViewModel.systemStatus.observe(this@ConsoleFragment) {
                tvCpuUsage.text = getString(R.string.percent_format, it.cpu)
                spvCpuUsage.setProgressSmooth(it.cpu / 100)
                tvMemoryUsage.text = getString(R.string.percent_format, it.memory)
                spvMemoryUsage.setProgressSmooth(it.memory / 100)
                tvDiskUsage.text = getString(R.string.percent_format, it.disk)
                spvDiskUsage.setProgressSmooth(it.disk / 100)
            }
        }
        icInformation.run {
            mViewModel.botList.observe(this@ConsoleFragment) { list ->
                list.find { it.isSelect }?.let { info ->
                    GlideApp.with(ivAvatar)
                        .load(info.avatarUrl)
                        .into(ivAvatar)
                    tvNickname.text = info.nickname
                    tvId.text = info.selfId
                    tvFriendCount.text = info.friendCount.toString()
                    tvGroupCount.text = info.groupCount.toString()
                    startUpdateConnectedDuration(info.connectTime.toLong())

                    mViewModel.requestMessageCount(info.selfId)
                }
            }
        }
        icMessageCount.wvCharts.run {
            viewLifecycleScope.launch {
                mViewModel.messageCount.collect { data ->
                    mMessageCountWebClient.blockDuringLoading {
                        setChartData(
                            getString(
                                R.string.message_count_data_format,
                                data.day, data.week, data.month, data.year, data.num
                            ),
                            "name",
                            "count"
                        )
                    }
                }
            }
        }
        icActiveGroup.wvCharts.run {
            viewLifecycleScope.launch {
                mViewModel.activeGroup.collect { data ->
                    mActiveGroupWebClient.blockDuringLoading {
                        setChartData(data, "name", "chat_num", "group_id")
                    }
                }
            }
        }
        icPopularPlugin.wvCharts.run {
            viewLifecycleScope.launch {
                mViewModel.popularPlugin.collect { data ->
                    mActiveGroupWebClient.blockDuringLoading {
                        setChartData(data, "name", "count", "module")
                    }
                }
            }
        }
        mViewModel.requestBotList()
        mViewModel.requestActiveGroup()
        mViewModel.requestPopularPlugin()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.openWebSocket()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.closeWebSocket()
    }

    private fun startUpdateConnectedDuration(connectTime: Long) {
        mCountdownJob?.cancel()
        mCountdownJob = viewLifecycleScope.launch {
            while (isActive) {
                var diff = System.currentTimeMillis() / 1000 - connectTime
                val days = diff / 86400
                diff %= 86400
                val hours = diff / 3600
                diff %= 3600
                val minutes = diff / 60
                diff %= 60
                viewBinding.icInformation.tvConnectionDuration.text =
                    getString(R.string.connected_duration_format, days, hours, minutes, diff)
                delay(1000)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val isNightModeNow = newConfig.isNightMode()
        if (resources.configuration.isNightMode() != isNightModeNow) {
            viewBinding.icActiveGroup.wvCharts.setChartNightMode(isNightModeNow)
        }
    }

    private fun WebView.setChartNightMode(isNightMode: Boolean) {
        evaluateJavascript("javascript:setDarkMode($isNightMode)") {}
    }

    private fun WebView.setChartData(
        data: String,
        xField: String,
        yField: String,
        vararg fields: String
    ) {
        val builder = StringBuilder("javascript:setChartData($data,'$xField','$yField")
        for (field in fields) {
            builder.append("','").append(field)
        }
        builder.append("')")
        evaluateJavascript(builder.toString(), null)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.run {
            icMessageCount.wvCharts.detach()
            icActiveGroup.wvCharts.detach()
            icPopularPlugin.wvCharts.detach()
        }
    }

    class ConsoleChartWebViewClient(
        private val doOnPageFinished: (webView: WebView, url: String) -> Unit
    ) : WebViewClient() {
        private var mLoading = true

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            mLoading = false
            doOnPageFinished.invoke(view, url)
        }

        suspend fun blockDuringLoading(block: () -> Unit) {
            while (mLoading) delay(100)
            block.invoke()
        }
    }
}