package io.manticore.android.scanner;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

import io.manticore.android.concurent.ThreadPool;

public class NetworkScanner extends Thread {
    public static final String TAG = "NetScan";

    @Override
    public void run() {

        for(int i = 0; i < 255; i++) {
            final String host = "192.168.1." + i;
            ThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if(host.equals("192.168.1.254"))
                        Log.i(TAG , "complete");

                    try {
                        if (InetAddress.getByName(host).isReachable(1000)) {
                            Log.i(TAG, host + " is online");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
