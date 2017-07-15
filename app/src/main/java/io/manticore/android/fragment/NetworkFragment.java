package io.manticore.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.io.IOException;
import java.net.InetAddress;

import io.manticore.android.concurent.ThreadPool;
import io.manticore.android.scanner.NetworkScanner;

public class NetworkFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        new NetworkScanner().start();
    }

    @Override
    public void onResume() {
        super.onResume();
        ThreadPool.getInstance().resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ThreadPool.getInstance().pause();
    }
}
