package io.manticore.android.util;

import java.io.IOException;
import java.net.InetAddress;

import io.manticore.android.model.NetworkHost;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class NetworkScanner {

    private int threads;
    private Consumer<NetworkHost> consumer;

    public NetworkScanner(int threads) {
        this.threads = threads;
    }

    public void start(Consumer<NetworkHost> consumer) {
        this.consumer = consumer;
        
    }

    private void check(String host) {
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            long endTime;
            long startTime = System.nanoTime();
            if (inetAddress.isReachable(200)) {
                endTime = System.nanoTime();
                Observable.just(new NetworkHost(host, inetAddress.getCanonicalHostName(), (int) ((startTime - endTime) / 1000000000))).subscribe(consumer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
