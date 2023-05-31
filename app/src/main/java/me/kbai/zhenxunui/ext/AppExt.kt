package me.kbai.zhenxunui.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment


/**
 * @author sean on 2022/4/14
 */

fun Activity.startActivity(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
}

fun ComponentActivity.startActivity(
    clazz: Class<*>,
    requestCode: Int = -1,
    extras: Bundle? = null,
    options: Bundle? = null,
    finish: Boolean = false
) {
    @Suppress("DEPRECATION")
    startActivityForResult(
        Intent(this, clazz).apply { extras?.let { putExtras(it) } },
        requestCode,
        options
    )
    if (finish) finish()
}

fun Fragment.startActivity(
    clazz: Class<*>,
    requestCode: Int = -1,
    extras: Bundle? = null,
    options: Bundle? = null,
    finish: Boolean = false
) {
    @Suppress("DEPRECATION")
    startActivityForResult(
        Intent(requireContext(), clazz).apply { extras?.let { putExtras(it) } },
        requestCode,
        options
    )
    if (finish) requireActivity().finish()
}

fun Activity.displaySize(): Point {
    val point0 = Point()
    val point1 = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display?.getCurrentSizeRange(point0, point1)
    } else {
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getCurrentSizeRange(point0, point1)
    }
    return when (resources.configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> Point(point1.x, point0.y)
        else -> Point(point0.x, point1.y)
    }
}

fun Context.displaySize(): Point {
    val point0 = Point()
    val point1 = Point()
    @Suppress("DEPRECATION")
    getSystemService<WindowManager>()!!.defaultDisplay.getCurrentSizeRange(point0, point1)
    return when (resources.configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> Point(point1.x, point0.y)
        else -> Point(point0.x, point1.y)
    }
}

fun Context.getNavigationHeight(): Int {
    val resourceId = resources.getIdentifier(
        "navigation_bar_height",
        "dimen",
        "android"
    )
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun Context.getStatusBarHeight(): Int {
    val resourceId: Int = resources.getIdentifier(
        "status_bar_height",
        "dimen",
        "android"
    )
    return resources.getDimensionPixelSize(resourceId)
}

fun Activity.grantUriPermission(intent: Intent, uri: Uri) {
    val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    for (info in list) {
        grantUriPermission(
            info.activityInfo.packageName,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }
}

fun FragmentActivity.findNavControllerByManager(@IdRes id: Int) =
    (supportFragmentManager.findFragmentById(id) as NavHostFragment).navController

fun Activity.showToast(s: String, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(this, s, duration).show()

fun Fragment.showToast(s: String, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(context, s, duration).show()

fun Activity.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(this, resId, duration).show()

fun Fragment.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(context, resId, duration).show()