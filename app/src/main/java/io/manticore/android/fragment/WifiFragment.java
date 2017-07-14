package io.manticore.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;
import io.manticore.android.model.AccessPoint;
import io.manticore.android.scanner.WifiScanner;

import static io.manticore.android.util.WifiUtils.getWifiManager;

/**
 * A fragment used for scanning wifi networks.
 */
public class WifiFragment extends Fragment {

    protected @BindView(R.id.ap_listview) RecyclerView mListView;

    private Context mContext;
    private Handler mHandler;
    private WifiScanner mScanner;
    private FastItemAdapter<AccessPoint> mAdapter = new FastItemAdapter<>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        if (!getWifiManager(mContext).isWifiEnabled())
            getWifiManager(mContext).setWifiEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        mHandler = new Handler();
        mScanner = new WifiScanner(mHandler, mContext, mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mScanner.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mScanner.pause();
    }

    @Override
    public void onStop() {
        super.onStop();

        mHandler.removeCallbacks(mScanner);
    }
}
