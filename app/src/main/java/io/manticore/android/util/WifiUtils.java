package io.manticore.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class WifiUtils {

    @SuppressLint("WifiManagerPotentialLeak")
    public static WifiManager getWifiManager(Context context) {

        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static boolean isOnWifi(Context context) {

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
}