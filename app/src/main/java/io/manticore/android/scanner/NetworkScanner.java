package io.manticore.android.scanner;

import java.io.IOException;
import java.net.InetAddress;

import io.manticore.android.concurent.ThreadPool;

public class NetworkScanner implements Runnable {
    public static final String TAG = "Network";

    @Override
    public void run() {

        for(int i = 0; i < 255; i++) {
            final String host = "192.168.1." + i;
            ThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (InetAddress.getByName(host).isReachable(1000)) {
                            System.out.println(host + " is online");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
