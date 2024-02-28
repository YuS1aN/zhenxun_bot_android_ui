package me.kbai.zhenxunui.extends

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.SeekBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

/**
 * @author sean on 2022/4/15
 */

val Number.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, toFloat(), Resources.getSystem().displayMetrics
    )

val Number.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, toFloat(), Resources.getSystem().displayMetrics
    )

fun View.setOnDebounceClickListener(delay: Int = 500, listener: View.OnClickListener) {
    setOnDebounceClickListener(delay) { listener.onClick(it) }
}

inline fun View.setOnDebounceClickListener(
    delay: Int = 500, crossinline listener: (view: View) -> Unit
) {
    var last = 0L
    setOnClickListener {
        val cur = System.currentTimeMillis()
        if (cur - last > delay) {
            listener.invoke(it)
            last = cur
        }
    }
}

fun EditText.hideSoftInputWhenInputDone() = hideSoftInputWhenInputDone(null)
fun EditText.hideSoftInputWhenInputDone(next: View?) =
    setOnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE || event != null) {
            v.context.getSystemService(InputMethodManager::class.java)
                .hideSoftInputFromWindow(v.windowToken, 0)
            next?.requestFocus()
            return@setOnEditorActionListener true
        }
        false
    }

fun View.removeFromParent(requestLayout: Boolean = true) {
    val parent = parent
    if (parent is ViewGroup) {
        if (requestLayout) parent.removeView(this)
        else parent.removeViewInLayout(this)
    }
}

inline fun TabLayout.addOnTabSelectedListener(
    crossinline selected: (tab: TabLayout.Tab) -> Unit = {},
    crossinline unselected: (tab: TabLayout.Tab) -> Unit = {},
    crossinline reselected: (tab: TabLayout.Tab) -> Unit = {}
): OnTabSelectedListener {
    val listener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            selected.invoke(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            unselected.invoke(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            reselected.invoke(tab)
        }
    }
    addOnTabSelectedListener(listener)
    return listener
}

fun WebView.detach() {
    loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
    clearHistory()
    removeFromParent(false)
    //destroy()
}

inline fun SeekBar.setOnProgressChangedListener(crossinline block: SeekBar.(progress: Int, fromUser: Boolean) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            block(seekBar, progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    })
}