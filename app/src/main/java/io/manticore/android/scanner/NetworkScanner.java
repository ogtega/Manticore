package io.manticore.android.scanner;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                    String _address = "192.168.1." + host;
                    String msg = "Scanning " + _address;

                    try {

                        InetAddress address = InetAddress.getByName(_address);

                        if (address.isReachable(1000)) {
                            Log.i(Thread.currentThread().getName(), msg + " successful");
                            Observable.just(new NetworkHost(_address, address.getHostName())).subscribe(consumer);
                        } else {
                            Log.i(Thread.currentThread().getName(), msg + " failed");
                            Observable.just(new NetworkHost(_address)).subscribe(consumer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private String getMac() throws IOException, InterruptedException {
        String res = null;

        //TODO: Get the arp cache and parse it for the mac
        //Process process = Runtime.getRuntime().exec("cat /proc/net/arp");

        return res;
    }
}
