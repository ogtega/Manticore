package io.manticore.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import io.manticore.android.concurent.ThreadPool;

import static io.manticore.android.util.WiFiUtils.getDhcpInfo;
import static io.manticore.android.util.WiFiUtils.intToIPv4;

public class App extends Application {
    private static App instance;

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
