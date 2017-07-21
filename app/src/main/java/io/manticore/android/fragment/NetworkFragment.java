package io.manticore.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.manticore.android.R;
import io.manticore.android.concurent.ThreadPool;
import io.manticore.android.scanner.NetworkScanner;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static io.manticore.android.scanner.NetworkScanner.TAG;

public class NetworkFragment extends Fragment {
    NetworkScanner scanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        scanner = new NetworkScanner(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.i(TAG, s);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        scanner.start();
        ThreadPool.getInstance().resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ThreadPool.getInstance().pause();
    }
}
