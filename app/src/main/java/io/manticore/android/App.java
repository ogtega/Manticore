package io.manticore.android;

import android.app.Application;
import android.support.annotation.NonNull;

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    @NonNull
    public static App getInstance() {
        return instance;
    }

    private void init() {

    }
}
