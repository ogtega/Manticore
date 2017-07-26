package io.manticore.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;
import io.manticore.android.concurent.ThreadPool;
import io.manticore.android.model.NetworkHost;
import io.manticore.android.scanner.NetworkScanner;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static io.manticore.android.util.WiFiUtils.isWiFiEnabled;

public class NetworkFragment extends Fragment {

    protected @BindView(R.id.ap_listview) RecyclerView mListView;
    protected @BindView(R.id.swipe_refresh) SwipeRefreshLayout mRefreshLayout;

    private int count;
    private long start;
    NetworkScanner scanner;
    private FastItemAdapter<NetworkHost> mAdapter = new FastItemAdapter<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRefreshLayout.setColorSchemeResources(R.color.accent);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                count = 0;
                startScan();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startScan();
    }

    public void startScan() {

        if (isWiFiEnabled()) {
            start = System.nanoTime();

            mRefreshLayout.setRefreshing(true);
            scanner = new NetworkScanner(new Consumer<NetworkHost>() {
                @Override
                public void accept(@NonNull final NetworkHost host) throws Exception {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!ThreadPool.getInstance().isShutdown()) {
                                count++;

                                if (host.isOnline()) {
                                    for(int i = 0; i < mAdapter.getAdapterItems().size(); i++) {
                                        if(mAdapter.getAdapterItem(i).getMac().equals(host.getMac())) {
                                            return;
                                        }
                                    }

                                    List<NetworkHost> hosts = mAdapter.getAdapterItems();
                                    hosts.add(host);
                                    Collections.sort(hosts, new NetworkHost.HostComparator());
                                    mAdapter.setNewList(hosts);
                                }

                                if (count == 255) {
                                    mRefreshLayout.setRefreshing(false);
                                    Log.i(Thread.currentThread().getName(), String.format("Found %d online hosts in %.3fs %n", mAdapter.getAdapterItemCount(), (System.nanoTime() - start) / 1e9));
                                }
                            }
                        }
                    });
                }
            }, mAdapter.getAdapterItems());

            scanner.start();
        } else {

            startScan();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ThreadPool.getInstance().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scanner.interrupt();
    }
}
