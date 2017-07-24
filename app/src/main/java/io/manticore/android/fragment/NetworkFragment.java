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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;
import io.manticore.android.concurent.ThreadPool;
import io.manticore.android.model.NetworkHost;
import io.manticore.android.scanner.NetworkScanner;
import io.manticore.android.util.NetUtils;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class NetworkFragment extends Fragment {

    protected @BindView(R.id.ap_listview) RecyclerView mListView;
    protected @BindView(R.id.swipe_refresh) SwipeRefreshLayout mRefreshLayout;

    private long start;
    volatile private int count;
    private NetworkScanner scanner;
    private FastItemAdapter<NetworkHost> mAdapter = new FastItemAdapter<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(count == 255) {
                    count = 0;
                    startScan();
                }
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

        if (NetUtils.onWifi(getActivity())) {
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
                                    mAdapter.add(host);
                                }

                                if (count == 255) {
                                    mRefreshLayout.setRefreshing(false);
                                    Log.i(Thread.currentThread().getName(), String.format("Conducted %d successful scans in %.3fs %n", mAdapter.getAdapterItemCount(), (System.nanoTime() - start) / 1e9));
                                }
                            }
                        }
                    });
                }
            });

            scanner.start();
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
        ThreadPool.getInstance().getQueue().clear();
    }
}
