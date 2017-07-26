package io.manticore.android.scanner;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.manticore.android.concurent.ThreadPool;
import io.manticore.android.model.NetworkHost;
import io.manticore.android.util.ThreadUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkScanner extends Thread {
    private Consumer<NetworkHost> consumer;
    private OkHttpClient client = new OkHttpClient();
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

                            String mac = getMac(_address);
                            String vendor = getVendor(mac);
                            Observable.just(new NetworkHost(host, _address, address.getHostName(), mac, vendor)).subscribe(consumer);
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

    private String getVendor(String mac) throws IOException {
        String result = "";

        if(!mac.isEmpty()) {
            String url = ("http://api.macvendors.com/"
                    + URLEncoder.encode(mac.substring(0, mac.length() / 2), "UTF-8"));

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.body() != null) {
                result = response.body().string();
                response.body().close();
            }
        } else {
            return "This device";
        }

        return result;
    }

    private String getMac(String ip) throws IOException {
        Pattern pattern = Pattern.compile("([0-9a-fA-F]{2}[:]){5}[0-9a-fA-F][0-9a-fA-F]");

        try (BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (line.contains(ip + ' ')) {
                    Matcher matcher = pattern.matcher(line);

                    if (matcher.find()) {
                        return matcher.group();
                    }
                }
            }
        }

        return "";
    }
}
