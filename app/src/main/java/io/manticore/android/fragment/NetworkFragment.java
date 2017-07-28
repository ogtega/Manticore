package io.manticore.android.fragment;

import android.os.Build;
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

import java.net.SocketException;
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

import static io.manticore.android.util.WiFiUtils.getDhcpInfo;
import static io.manticore.android.util.WiFiUtils.getMac;
import static io.manticore.android.util.WiFiUtils.getWifiInfo;
import static io.manticore.android.util.WiFiUtils.intToIPv4;
import static io.manticore.android.util.WiFiUtils.isWiFiEnabled;

public class NetworkFragment extends Fragment {

    public int count;
    private long start;

    private NetworkScanner scanner;

    protected @BindView(R.id.ap_listview) RecyclerView mListView;
    protected @BindView(R.id.swipe_refresh) SwipeRefreshLayout mRefreshLayout;

    private final FastItemAdapter<NetworkHost> mAdapter = new FastItemAdapter<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        mAdapter.withSelectable(true);
        mAdapter.setHasStableIds(true);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRefreshLayout.setColorSchemeResources(R.color.accent);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                count = 0;
                scan();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scanner = new NetworkScanner(new Consumer<NetworkHost>() {

            @Override
            public void accept(@NonNull final NetworkHost host) throws Exception {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            count++;

                            if (host.isOnline()) {
                                for (int i = 0; i < mAdapter.getAdapterItems().size(); i++) {
                                    if (mAdapter.getAdapterItem(i).getMac().equals(host.getMac())) {
                                        return;
                                    }
                                }

                                if (host.getIp().equals(intToIPv4(getDhcpInfo().gateway))) {
                                    host.setHostname(getWifiInfo().getSSID());
                                } else try {
                                    if(host.getMac().equals(getMac())) {
                                        host.setHostname(Build.MODEL + ' ' + "(This Device)");
                                    }
                                } catch (SocketException e) {
                                    e.printStackTrace();
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
                    });
                }
            }
        });

        scan();
    }

    public void scan() {

        if (isWiFiEnabled()) {
            start = System.nanoTime();

            mRefreshLayout.setRefreshing(true);
            ThreadPool.getInstance().execute(scanner);
        } else {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        ThreadPool.getInstance().pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ThreadPool.getInstance().resume();
    }

    @Override
    public void onDestroy() {
        ThreadPool.getInstance().clean();
        super.onDestroy();
    }
}
