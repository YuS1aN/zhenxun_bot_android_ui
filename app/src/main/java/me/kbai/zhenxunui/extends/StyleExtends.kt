package me.kbai.zhenxunui.extends

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.ColorInt
import me.kbai.zhenxunui.R

/**
 * @author sean
 */
fun Resources.Theme.getThemeValue(resId: Int): TypedValue? {
    val typedValue = TypedValue()
    if (!resolveAttribute(resId, typedValue, true)) {
        return null
    }
    return typedValue
}

@ColorInt
fun Context.getThemeColor(attr: Int): Int {
    val res = theme.getThemeValue(attr)?.resourceId
        ?: R.color.white
    return getColor(res)
}