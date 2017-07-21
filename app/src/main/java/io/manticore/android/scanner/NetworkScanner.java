package io.manticore.android.scanner;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

import io.manticore.android.concurent.ThreadPool;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class NetworkScanner extends Thread {
    public static String TAG = "Network";

    private Consumer<String> consumer;

    public NetworkScanner(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        int b = 0;
        int offset = 1;
        int[] base = {0, 64, 128, 192};

        while(offset < 65) {
            for (int curr: base) {
                scan(curr + offset);
            }

            if(b != 3) {
                b++;
            } else {
                b = 0;
            }

            offset++;
        }
    }

    private void scan(final int host) {
        ThreadPool.getInstance().execute(new Thread() {
            @Override
            public void run() {
                String address = "192.168.1." + host;

                try {
                    if (InetAddress.getByName(address).isReachable(600)) {
                        Observable.just(address).subscribe(consumer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
