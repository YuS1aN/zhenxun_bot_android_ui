package me.kbai.zhenxunui.tool;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.StringRes;

/**
 * @author sean 2020/9/11
 */
public class GlobalToast {
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());
    private static Application sContext;

    private GlobalToast() {
    }

    public static void init(Application application) {
        sContext = application;
    }

    public static void showToastSafe(String s) {
        sMainHandler.post(() -> showToast(s));
    }

    public static void showToastSafe(String s, int duration) {
        sMainHandler.post(() -> showToast(s, duration));
    }

    public static void showToastSafe(@StringRes int strRes) {
        sMainHandler.post(() -> showToast(strRes));
    }

    public static void showToastSafe(@StringRes int strRes, int duration) {
        sMainHandler.post(() -> showToast(strRes, duration));
    }

    public static void showToast(String s) {
        showToast(s, Toast.LENGTH_LONG);
    }

    public static void showToast(@StringRes int strRes) {
        showToast(strRes, Toast.LENGTH_LONG);
    }

    public static void showToast(String s, int duration) {
        if (TextUtils.isEmpty(s)) return;
        Toast.makeText(sContext, s, duration).show();
    }

    public static void showToast(@StringRes int strRes, int duration) {
        Toast.makeText(sContext, strRes, duration).show();
    }
}
