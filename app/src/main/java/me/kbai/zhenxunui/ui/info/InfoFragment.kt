package me.kbai.zhenxunui.ui.info

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentInfoBinding
import me.kbai.zhenxunui.extends.detach
import me.kbai.zhenxunui.extends.isNightMode
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.viewmodel.InfoViewModel

/**
 * @author Sean on 2023/5/30
 */
class InfoFragment : BaseFragment<FragmentInfoBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoBinding = FragmentInfoBinding.inflate(inflater)

    private val mViewModel by viewModels<InfoViewModel>()
    private var mPageFinished = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        viewBinding.wvCharts.settings.run {
            javaScriptEnabled = true
            setSupportZoom(false)
            setNeedInitialFocus(false)
            allowFileAccess = true
            builtInZoomControls = true
            loadWithOverviewMode = true
            useWideViewPort = true
            loadsImagesAutomatically = true
        }
        viewBinding.wvCharts.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mPageFinished = true

                val isNightMode =
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                viewBinding.wvCharts.evaluateJavascript("javascript:setDarkMode($isNightMode)") {}
            }
        }
        viewBinding.wvCharts.setBackgroundColor(0)
        viewBinding.wvCharts.loadUrl("file:///android_asset/info_charts.html")
    }

    private suspend fun waitingForPageFinish() {
        while (!mPageFinished) {
            delay(100)
        }
    }

    override fun initData() {
        val voidCallback = ValueCallback<String> {}
        viewLifecycleScope.launch {
            mViewModel.pollingStatusList()
        }
        viewLifecycleScope.launch {
            mViewModel.statusList.collect {
                waitingForPageFinish()
                viewBinding.wvCharts.evaluateJavascript(
                    "javascript:setLineChartsData($it)",
                    voidCallback
                )
            }
        }
        viewLifecycleScope.launch {
            mViewModel.diskUsage.collect {
                waitingForPageFinish()
                viewBinding.wvCharts.evaluateJavascript(
                    "javascript:setDiskChartData($it)",
                    voidCallback
                )
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!mPageFinished) return

        val isNightModeNow = newConfig.isNightMode()
        if (resources.configuration.isNightMode() != isNightModeNow)
            viewBinding.wvCharts.evaluateJavascript("javascript:setDarkMode($isNightModeNow)") {}
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.wvCharts.detach()
    }
}