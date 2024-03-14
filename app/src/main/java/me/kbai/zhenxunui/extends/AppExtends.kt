package me.kbai.zhenxunui.extends

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import me.kbai.zhenxunui.ZxApplication
import me.kbai.zhenxunui.api.BotApi
import me.kbai.zhenxunui.ui.LoginActivity


/**
 * @author sean on 2022/4/14
 */

fun application(): Application = ZxApplication.getApplication()

fun Application.logout() {
    BotApi.authorization = ""
    val intent = Intent(this, LoginActivity::class.java)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(intent)
}

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

fun Window.displaySize() = windowManager.displaySize()

fun WindowManager.displaySize(): Size =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics = currentWindowMetrics
        val windowInsets = metrics.windowInsets
        val insets = windowInsets.getInsets(
            WindowInsets.Type.navigationBars()
                    or WindowInsets.Type.statusBars()
                    or WindowInsets.Type.displayCutout()
        )
        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom
        val bounds = metrics.bounds
        Size(
            bounds.width() - insetsWidth,
            bounds.height() - insetsHeight
        )
    } else {
        val point = Point()
        @Suppress("DEPRECATION")
        defaultDisplay.getSize(point)
        Size(point.x, point.y)
    }

fun Context.displaySize(): Size {
    val point0 = Point()
    val point1 = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display
    } else {
        @Suppress("DEPRECATION")
        getSystemService<WindowManager>()?.defaultDisplay
    }?.getCurrentSizeRange(point0, point1)

    return when (resources.configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> Size(point1.x, point0.y)
        else -> Size(point0.x, point1.y)
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

fun Configuration.isNightMode(): Boolean =
    uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
