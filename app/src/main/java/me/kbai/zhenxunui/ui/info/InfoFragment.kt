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
import me.kbai.zhenxunui.ext.viewLifecycleScope
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
        viewBinding.wvTest.settings.run {
            javaScriptEnabled = true
            setSupportZoom(false)
            setNeedInitialFocus(false)
            allowFileAccess = true
            builtInZoomControls = true
            loadWithOverviewMode = true
            useWideViewPort = true
            loadsImagesAutomatically = true
        }
        viewBinding.wvTest.loadUrl("file:///android_asset/info_charts.html")
        viewBinding.wvTest.setBackgroundColor(0)

        viewBinding.wvTest.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mPageFinished = true

                val isNightMode =
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                viewBinding.wvTest.evaluateJavascript("javascript:setDarkMode($isNightMode)") {}
            }
        }

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
                viewBinding.wvTest.evaluateJavascript(
                    "javascript:setLineChartsData($it)",
                    voidCallback
                )
            }
        }
        viewLifecycleScope.launch {
            mViewModel.diskUsage.collect {
                waitingForPageFinish()
                viewBinding.wvTest.evaluateJavascript(
                    "javascript:setDiskChartData($it)",
                    voidCallback
                )
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!mPageFinished) return

        val isNightModeBefore =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val isNightModeNow =
            newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isNightModeBefore != isNightModeNow)
            viewBinding.wvTest.evaluateJavascript("javascript:setDarkMode($isNightModeNow)") {}
    }
}