package me.kbai.zhenxunui.ui.console

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.PointF
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentConsoleBinding
import me.kbai.zhenxunui.extends.detach
import me.kbai.zhenxunui.extends.isNightMode
import me.kbai.zhenxunui.extends.launchAndCollectIn
import me.kbai.zhenxunui.extends.logI
import me.kbai.zhenxunui.extends.runWithoutReturn
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.viewmodel.ConsoleViewModel
import me.kbai.zhenxunui.viewmodel.MainViewModel
import kotlin.math.abs

/**
 * @author Sean on 2023/5/30
 */
class ConsoleFragment : BaseFragment<FragmentConsoleBinding>() {

    companion object {
        const val LOCAL_DOMAIN = "local.host"
        const val BAR_CHART_FILE = "http://$LOCAL_DOMAIN/assets/console_bar_charts.html"
        const val TANGENTIAL_BAR_FILE =
            "http://$LOCAL_DOMAIN/assets/console_horizontal_bar_charts.html"
    }

    private val mMainViewModel by viewModels<MainViewModel>({ requireActivity() })
    private val mViewModel by viewModels<ConsoleViewModel>()

    private var mCountdownJob: Job? = null

    private lateinit var mMessageCountWebClient: ConsoleChartWebViewClient
    private lateinit var mActiveGroupWebClient: ConsoleChartWebViewClient
    private lateinit var mPopularPluginWebClient: ConsoleChartWebViewClient
    private val mAssetLoader by lazy {
        WebViewAssetLoader.Builder()
            .setDomain(LOCAL_DOMAIN)
            .setHttpAllowed(true)
            .addPathHandler("/assets/", AssetsPathHandler(requireContext()))
            .build()
    }

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentConsoleBinding = FragmentConsoleBinding.inflate(inflater, container, false)

    @SuppressLint("ClickableViewAccessibility")
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
        tvLogs.run {
            movementMethod = ScrollingMovementMethod.getInstance()

            var lastPoint: PointF? = null

            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastPoint = PointF(event.x, event.y)
                        v.parent.parent.requestDisallowInterceptTouchEvent(true)
                    }

                    MotionEvent.ACTION_MOVE -> lastPoint?.let {
                        val dy = event.y - it.y
                        if (abs(dy) > 10) {
                            v.parent.requestDisallowInterceptTouchEvent(true)
                        }
                        if (dy > 0) {
                            v.parent.parent.requestDisallowInterceptTouchEvent(canScrollVertically(-1))
                        } else if (dy < 0) {
                            v.parent.parent.requestDisallowInterceptTouchEvent(canScrollVertically(1))
                        }
                        it.set(event.x, event.y)
                        logI("DY:$dy  SY:$scrollY  SB:${lineCount * lineHeight - height}")
                    }

                    MotionEvent.ACTION_UP -> {
                        lastPoint = null
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        v.parent.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }
        }
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

    override fun initData() = viewBinding.runWithoutReturn {
        icSystemStatus.run {
            mViewModel.systemStatus.observe(viewLifecycleOwner) {
                tvCpuUsage.text = getString(R.string.percent_format, it.cpu)
                spvCpuUsage.setProgressSmooth(it.cpu / 100)
                tvMemoryUsage.text = getString(R.string.percent_format, it.memory)
                spvMemoryUsage.setProgressSmooth(it.memory / 100)
                tvDiskUsage.text = getString(R.string.percent_format, it.disk)
                spvDiskUsage.setProgressSmooth(it.disk / 100)
            }
        }
        icInformation.run {
            mMainViewModel.currentBot.observe(viewLifecycleOwner) { info ->
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
        mViewModel.messageCount.launchAndCollectIn(this@ConsoleFragment) { data ->
            mMessageCountWebClient.blockDuringLoading {
                icMessageCount.wvCharts.setChartData(
                    getString(
                        R.string.message_count_data_format,
                        data.day, data.week, data.month, data.year, data.num
                    ),
                    "name",
                    "count"
                )
            }
        }
        mViewModel.activeGroup.launchAndCollectIn(this@ConsoleFragment) { data ->
            mActiveGroupWebClient.blockDuringLoading {
                icActiveGroup.wvCharts.setChartData(data, "name", "chat_num", "group_id")
            }
        }
        mViewModel.popularPlugin.launchAndCollectIn(this@ConsoleFragment) { data ->
            mActiveGroupWebClient.blockDuringLoading {
                icPopularPlugin.wvCharts.setChartData(data, "name", "count", "module")
            }
        }
        mViewModel.botLogs.launchAndCollectIn(this@ConsoleFragment) { data ->
            tvLogs.run {
                text = data.obj
                post {
                    val y = lineCount * lineHeight - height
                    if (y > height) {
                        scrollTo(0, y)
                    }
                }
            }
        }

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
        viewBinding?.run {
            icMessageCount.wvCharts.detach()
            icActiveGroup.wvCharts.detach()
            icPopularPlugin.wvCharts.detach()
        }
    }
}

class ConsoleChartWebViewClient(
    private val assetsLoader: WebViewAssetLoader,
    private val doOnPageFinished: (webView: WebView, url: String) -> Unit
) : WebViewClient() {
    private var mLoading = true

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        mLoading = false
        doOnPageFinished.invoke(view, url)
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return assetsLoader.shouldInterceptRequest(request.url)
    }

    suspend fun blockDuringLoading(block: () -> Unit) {
        while (mLoading) delay(100)
        block.invoke()
    }
}