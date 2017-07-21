package io.manticore.android.scanner;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

import io.manticore.android.concurent.ThreadPool;

public class NetworkScanner extends Thread {
    public static final String TAG = "Network";

    @Override
    public void run() {
        int min = 1;
        int max = 254;
        int offset = 0;
        boolean b = true;

        while(offset != 127) {

            final int curr = b? min + offset:max - offset;

            check(curr);

            if(!b) {offset++;}
            b = !b;
        }
    }

    private void check(final int pos) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final String host = "192.168.1." + pos;

                try {
                    if (InetAddress.getByName(host).isReachable(600)) {
                        Log.i(TAG, host + " is online");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
