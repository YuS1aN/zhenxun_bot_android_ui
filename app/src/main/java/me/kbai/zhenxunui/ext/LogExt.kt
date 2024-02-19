package me.kbai.zhenxunui.ext

import android.util.Log
import me.kbai.zhenxunui.BuildConfig

/**
 * @author sean on 2022/5/12
 */

private inline fun whenDebug(action: () -> Int): Int {
    if (BuildConfig.DEBUG) {
        return action.invoke()
    }
    return 0
}

private fun outputLogFile(msg: String, tag: String) {
    //
}

fun Any.logI(msg: String, tag: String? = null): Int {
    val tagOrUnknown = tag ?: this::class.simpleName ?: "INFO"
    outputLogFile(msg, tagOrUnknown)
    return Log.i(tagOrUnknown, msg)
}

fun Any.logE(t: Throwable) {
    logE(t.message ?: t.toString())
    t.printStackTrace()
}

fun Any.logE(msg: String, tag: String? = null): Int {
    val tagOrUnknown = tag ?: this::class.simpleName ?: "ERROR"
    outputLogFile(msg, tagOrUnknown)
    return Log.e(tagOrUnknown, msg)
}

fun Any.logD(msg: String, tag: String? = null): Int = whenDebug {
    val tagOrUnknown = tag ?: this::class.simpleName ?: "DEBUG"
    outputLogFile(msg, tagOrUnknown)
    Log.d(tagOrUnknown, msg)
}