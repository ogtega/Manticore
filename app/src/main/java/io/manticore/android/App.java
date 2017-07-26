package io.manticore.android;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    private void init() {

    }
}
