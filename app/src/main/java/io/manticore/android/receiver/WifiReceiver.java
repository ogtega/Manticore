package io.manticore.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static io.manticore.android.util.WifiUtils.getWifiManager;

public class WifiReceiver extends BroadcastReceiver {

    private final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    private Consumer<ScanResult> consumer;

    public WifiReceiver(Consumer<ScanResult> consumer) {
        this.consumer = consumer;
    }

    public void start(Context context) {

        context.getApplicationContext().registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

            Observable.fromIterable(getWifiManager(context.getApplicationContext()).getScanResults()).subscribe(consumer);
        }
    }
}