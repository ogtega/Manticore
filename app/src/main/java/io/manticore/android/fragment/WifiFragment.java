package io.manticore.android.fragment;

import android.content.Context;
import android.net.wifi.ScanResult;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;
import io.manticore.android.model.AccessPoint;
import io.manticore.android.receiver.WifiReceiver;
import io.reactivex.functions.Consumer;

import static io.manticore.android.util.WifiUtils.getWifiManager;

/**
 * A fragment used for scanning wifi networks.
 */
public class WifiFragment extends Fragment {

    protected @BindView(R.id.ap_listview) RecyclerView mListView;

    private Context mContext;
    private WifiReceiver receiver;
    ScheduledExecutorService mExecutor;
    private FastItemAdapter<AccessPoint> mAdapter = new FastItemAdapter<>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        if (!getWifiManager(mContext).isWifiEnabled())
            getWifiManager(mContext).setWifiEnabled(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExecutor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        receiver = new WifiReceiver(new Consumer<ScanResult>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull ScanResult result) throws Exception {
                // Check if the result is a duplicate access point
                for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
                    if (mAdapter.getItem(i).matches(result.BSSID)) {
                        return;
                    }
                }

                Log.i(WifiReceiver.TAG, result.BSSID);
                mAdapter.add(new AccessPoint(result));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                receiver.scan(mContext.getApplicationContext());
            }
        }, 0, 3, TimeUnit.SECONDS);
    }
}