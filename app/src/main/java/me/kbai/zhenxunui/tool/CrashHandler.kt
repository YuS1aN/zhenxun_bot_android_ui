package me.kbai.zhenxunui.tool

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.kbai.zhenxunui.extends.logE

object CrashHandler {

    fun init(context: Context) {
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            runBlocking(Dispatchers.IO) {
                log(context, exception)
            }
        }
    }

    private fun log(context: Context, throwable: Throwable) = logE(
        "MODEL: ${Build.MODEL}  " +
                "SDK: ${Build.VERSION.SDK_INT}  " +
                "CPU_ABI: ${Build.SUPPORTED_ABIS.contentToString()}  " +
                "VERSION: ${getPackageVersionName(context)}  " +
                "\n StackTrace: \n" +
                Log.getStackTraceString(throwable),
        "CRASH"
    )


    private fun getPackageVersionName(context: Context) = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: NameNotFoundException) {
        ""
    }
}