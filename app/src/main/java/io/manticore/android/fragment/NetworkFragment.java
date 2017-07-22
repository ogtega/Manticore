package io.manticore.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;
import io.manticore.android.concurent.ThreadPool;
import io.manticore.android.model.NetworkHost;
import io.manticore.android.scanner.NetworkScanner;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static io.manticore.android.scanner.NetworkScanner.TAG;

public class NetworkFragment extends Fragment {
    protected @BindView(R.id.ap_listview) RecyclerView mListView;

    private NetworkScanner scanner;
    private FastItemAdapter<NetworkHost> mAdapter = new FastItemAdapter<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);


        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        scanner = new NetworkScanner(new Consumer<String>() {
            @Override
            public void accept(@NonNull final String s) throws Exception {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.add(new NetworkHost(s));
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "scanning");
        scanner.start();
        ThreadPool.getInstance().resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ThreadPool.getInstance().pause();
    }
}
