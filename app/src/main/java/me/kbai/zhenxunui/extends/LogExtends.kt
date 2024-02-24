package me.kbai.zhenxunui.extends

import android.util.Log
import me.kbai.zhenxunui.BuildConfig

/**
 * @author sean on 2022/5/12
 */

fun Any.logI(msg: String, tag: String? = null): Int {
    val tagOrUnknown = tag ?: nameOrSuperclassName ?: "INFO"
    outputLogFile(msg, tagOrUnknown)
    return Log.i(tagOrUnknown, msg)
}

fun Any.logE(t: Throwable) {
    logE(t.message ?: t.toString())
    t.printStackTrace()
}

fun Any.logE(msg: String, tag: String? = null): Int {
    val tagOrUnknown = tag ?: nameOrSuperclassName ?: "ERROR"
    outputLogFile(msg, tagOrUnknown)
    return Log.e(tagOrUnknown, msg)
}

fun Any.logD(msg: String, tag: String? = null): Int = whenDebug {
    val tagOrUnknown = tag ?: nameOrSuperclassName ?: "DEBUG"
    outputLogFile(msg, tagOrUnknown)
    Log.d(tagOrUnknown, msg)
}

private inline fun whenDebug(action: () -> Int): Int {
    if (BuildConfig.DEBUG) {
        return action.invoke()
    }
    return 0
}

private fun outputLogFile(msg: String, tag: String) {
    //
}

/**
 * The simple name of the class, or its parent simple class name if it's an anonymous class.
 */
private val Any.nameOrSuperclassName: String?
    get() = this::class.simpleName
        ?: this::class.java.superclass?.simpleName
