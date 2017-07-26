package io.manticore.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import static io.manticore.android.App.getAppContext;

public class WiFiUtils {

    public static boolean isWiFiEnabled() {
        return getManager().isWifiEnabled();
    }

    @SuppressLint("WifiManagerLeak")
    private static WifiManager getManager() {
        return (WifiManager) getAppContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static String getMac() throws SocketException {

        List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface nif : all) {
            if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

            byte[] macBytes = nif.getHardwareAddress();
            if (macBytes == null) {
                return "";
            }

            StringBuilder res = new StringBuilder();
            for (byte b : macBytes) {
                res.append(String.format("%02X:", b));
            }

            if (res.length() > 0) {
                res.deleteCharAt(res.length() - 1);
            }
            return res.toString();
        }

        return "02:00:00:00:00:00";
    }
}
