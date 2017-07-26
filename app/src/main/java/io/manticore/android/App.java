package io.manticore.android;

import android.app.Application;
import android.content.Context;

import io.manticore.android.concurent.ThreadPool;

public class App extends Application {
    private static App instance;

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    private void init() {

    }
}
