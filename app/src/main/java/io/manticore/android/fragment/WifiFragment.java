package io.manticore.android.fragment;

import android.content.Context;
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

    private WifiReceiver receiver;
    ScheduledExecutorService mExecutor;
    private FastItemAdapter<AccessPoint> mAdapter = new FastItemAdapter<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!getWifiManager(getContext()).isWifiEnabled())
            getWifiManager(getContext()).setWifiEnabled(true);
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
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));

        receiver = new WifiReceiver(new Consumer<AccessPoint>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull AccessPoint ap) throws Exception {
                // Check if the result is a duplicate access point
                for (AccessPoint item : mAdapter.getAdapterItems()) {
                    if (item.matches(ap)) {
                        return;
                    }
                }

                Log.i(WifiReceiver.TAG, ap.getBSSID());
                mAdapter.add(ap);
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
                receiver.scan(getActivity().getApplicationContext());
            }
        }, 0, 3, TimeUnit.SECONDS);
    }
}