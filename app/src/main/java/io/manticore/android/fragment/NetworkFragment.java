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
import io.manticore.android.model.NetworkHost;
import io.manticore.android.scanner.NetworkScanner;
import io.manticore.android.util.NetUtils;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class NetworkFragment extends Fragment {

    private int count;
    private long start;

    protected NetworkScanner scanner;
    protected FastItemAdapter<NetworkHost> mAdapter = new FastItemAdapter<>();
    protected @BindView(R.id.ap_listview) RecyclerView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);


        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        scanner = new NetworkScanner(new Consumer<NetworkHost>() {
            @Override
            public void accept(@NonNull final NetworkHost host) throws Exception {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        count++;

                        if(host.isOnline())
                            mAdapter.add(host);

                        if(count == 255)
                            Log.i(Thread.currentThread().getName(), String.format("Conducted %d successful scans in %.3fs %n", mAdapter.getAdapterItemCount(), (System.nanoTime() - start)/1e9));
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (NetUtils.onWifi(getActivity())) {
            start = System.nanoTime();
            scanner.start();
        }
    }
}
