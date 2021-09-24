package com.nwld.defi.tools;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static MyApp instance;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public boolean hasMain;
}
