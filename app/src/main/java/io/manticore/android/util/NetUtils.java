package io.manticore.android.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static io.manticore.android.App.getInstance;

public class NetUtils {

    public static boolean onWifi() {

        NetworkInfo info = ((ConnectivityManager) getInstance().getApplicationContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
