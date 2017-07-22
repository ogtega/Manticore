package io.manticore.android.scanner;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import io.manticore.android.concurent.ThreadPool;
import io.manticore.android.model.NetworkHost;
import io.manticore.android.util.ThreadUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class NetworkScanner extends Thread {
    private Consumer<NetworkHost> consumer;
    private int[] bases = new int[ThreadUtils.getCores()];

    public NetworkScanner(Consumer<NetworkHost> consumer) {
        this.consumer = consumer;

        for (int i = 0; i < bases.length; i++) {
            bases[i] = (int) (i * Math.ceil(255.0 / bases.length));
        }

        Log.i(Thread.currentThread().getName(), Arrays.toString(bases));
    }

    @Override
    public void run() {
        int offset = 1;

        while (offset < bases[1] + 1) {
            for (int base : bases) {
                scan(base + offset);
            }

            offset++;
        }
    }

    private void scan(final int host) {
        if (host < 256) {

            ThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    String address = "192.168.1." + host;
                    String msg = "Scanning " + address;

                    try {
                        if (InetAddress.getByName(address).isReachable(1000)) {
                            Log.i(Thread.currentThread().getName(), msg + " successful");
                            String hostname = InetAddress.getByName(address).getHostName();
                            Observable.just(new NetworkHost(address, hostname, true)).subscribe(consumer);
                        } else {
                            Log.i(Thread.currentThread().getName(), msg + " failed");
                            Observable.just(new NetworkHost(address, null, false)).subscribe(consumer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
