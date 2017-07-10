package io.manticore.android.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class WifiScanner extends BroadcastReceiver {

    final static IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    private Consumer<ScanResult> consumer;

    public WifiScanner(Consumer<ScanResult> consumer) {
        this.consumer = consumer;
    }

    @SuppressLint("WifiManagerPotentialLeak")
    public static WifiManager getWifiManager(@NonNull Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public WifiScanner start(@NonNull Context context) {
        context.getApplicationContext().registerReceiver(this, filter);
        return this;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().matches(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            Observable.fromIterable(getWifiManager(context.getApplicationContext()).getScanResults()).subscribe(consumer);
        }
    }
}
