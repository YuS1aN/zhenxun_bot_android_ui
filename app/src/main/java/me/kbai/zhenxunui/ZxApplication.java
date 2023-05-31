package me.kbai.zhenxunui;

import android.app.Application;

/**
 * @author Sean on 2023/5/31
 */
public class ZxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init(this);
    }
}
