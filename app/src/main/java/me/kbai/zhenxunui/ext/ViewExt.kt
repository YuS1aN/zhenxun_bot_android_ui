package me.kbai.zhenxunui.ext

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

/**
 * @author sean on 2022/4/15
 */

val Number.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )

fun View.setOnDebounceClickListener(delay: Int = 500, listener: View.OnClickListener) {
    setOnDebounceClickListener(delay) { listener.onClick(it) }
}

inline fun View.setOnDebounceClickListener(
    delay: Int = 500,
    crossinline listener: (view: View) -> Unit
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

fun View.removeFromParent() {
    val parent = parent
    if (parent is ViewGroup) {
        parent.removeView(this)
    }
}

inline fun TabLayout.addOnTabSelectedListener(
    crossinline selected: (tab: TabLayout.Tab) -> Unit = {},
    crossinline unselected: (tab: TabLayout.Tab) -> Unit = {},
    crossinline reselected: (tab: TabLayout.Tab) -> Unit = {}
) {
    addOnTabSelectedListener(object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            selected.invoke(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            unselected.invoke(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            reselected.invoke(tab)
        }
    })
}