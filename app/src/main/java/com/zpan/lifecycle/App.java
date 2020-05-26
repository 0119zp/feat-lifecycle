package com.zpan.lifecycle;

import android.app.Application;

/**
 * @author zpan
 * @date 2020/5/26 11:08 AM
 *
 * description: TODO
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(ActivityManager.getInstance());
    }
}
