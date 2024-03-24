package me.kbai.zhenxunui.ui.group

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import androidx.viewbinding.ViewBinding
import androidx.webkit.WebViewAssetLoader
import com.google.gson.JsonArray
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.extends.isNightMode
import me.kbai.zhenxunui.ui.console.ConsoleChartWebViewClient
import me.kbai.zhenxunui.ui.console.ConsoleFragment

abstract class BaseEditInfoFragment<VB : ViewBinding> : BaseFragment<VB>() {

    private val mAssetLoader by lazy {
        WebViewAssetLoader.Builder()
            .setDomain(ConsoleFragment.LOCAL_DOMAIN)
            .setHttpAllowed(true)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .build()
    }

    protected abstract fun getWebViews(): Array<out WebView>

    protected abstract fun updateInfo(): Any

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val isNightModeNow = newConfig.isNightMode()
        if (resources.configuration.isNightMode() != isNightModeNow) {
            getWebViews().forEach { it.setChartNightMode(isNightModeNow) }
        }
    }

    override fun onDestroyView() {
        getWebViews().forEach { it.destroy() }
        super.onDestroyView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected fun WebView.initChartWebView(path: String): ConsoleChartWebViewClient {
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

    protected fun WebView.setChartData(data: Map<String, Int>) {
        val array = JsonArray()
        data.forEach {
            val obj = JsonArray()
            obj.add(it.key)
            obj.add(it.value)
            array.add(obj)
        }
        evaluateJavascript("javascript:setChartData2($array)", null)
    }

    protected fun getSpannedCount(@StringRes id: Int, count: Int) =
        SpannableString(getString(id, count)).apply {
            val colorSpan = ForegroundColorSpan(
                ResourcesCompat.getColor(resources, R.color.text_gray, null)
            )
            setSpan(colorSpan, 0, indexOf(':') + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

    protected fun addSaveButton() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.group_info_menu_items, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.btn_save) {
                    updateInfo()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner)
    }
}