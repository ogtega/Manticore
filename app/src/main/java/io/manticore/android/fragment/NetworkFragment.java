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

    private NetworkScanner mScanner;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScanner = new NetworkScanner();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScanner.run();
    }
}
