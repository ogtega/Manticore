package io.manticore.android.scanner;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.util.Log;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import io.manticore.android.MainActivity;
import io.manticore.android.model.AccessPoint;
import io.manticore.android.receiver.WifiReceiver;
import io.manticore.android.util.WifiUtils;
import io.reactivex.functions.Consumer;

import static io.manticore.android.util.WifiUtils.getWifiManager;

public class WifiScanner implements Runnable {

    private final String TAG = "WiFi";

    private Handler handler;
    private Context context;
    private volatile boolean paused;
    private FastItemAdapter<AccessPoint> adapter;

    public WifiScanner(Handler handler, Context context, FastItemAdapter<AccessPoint> adapter) {

        this.handler = handler;
        this.context = context;
        this.adapter = adapter;

        handler.post(this);
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        Log.i(TAG, "Scanning");

        while (true) {
            if (!(paused)) break;
        }

        if(WifiUtils.isOnWifi(context)) {

            ((MainActivity) context).methodPrompt();
        } else {

            getWifiManager(context.getApplicationContext()).startScan();

            new WifiReceiver(new Consumer<ScanResult>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull ScanResult result) throws Exception {
                    // Check if the result is a duplicate access point
                    for (int i = 0; i < adapter.getAdapterItemCount(); i++) {
                        if (adapter.getItem(i).matches(result.BSSID)) {
                            return;
                        }
                    }

                    Log.i(TAG, result.BSSID);
                    adapter.add(new AccessPoint(result));
                }
            }).start(context);

            handler.postDelayed(this, 3000);
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }
}
