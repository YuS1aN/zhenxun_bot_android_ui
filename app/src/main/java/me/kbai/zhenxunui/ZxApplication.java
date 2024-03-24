package me.kbai.zhenxunui;

import android.app.Application;

import me.kbai.zhenxunui.tool.CrashHandler;
import me.kbai.zhenxunui.tool.GlobalToast;

/**
 * @author Sean on 2023/5/31
 */
public class ZxApplication extends Application {
    private static Application mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Constants.init(this);
        GlobalToast.init(this);
        CrashHandler.INSTANCE.init(this);
    }

    public static Application getApplication() {
        return mApp;
    }
}
