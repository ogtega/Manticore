package io.manticore.android.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.MainActivity;
import io.manticore.android.R;
import io.manticore.android.model.AccessPoint;
import io.manticore.android.util.NetUtils;
import io.reactivex.functions.Consumer;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static io.manticore.android.util.WifiScanner.getWifiManager;

/**
 * A fragment used for scanning wifi networks.
 */
public class WifiScanner extends Fragment {

    private final int RC_COARSE_LOCATION = 0xFFFF;
    public static final String TAG = "WifiScanner";

    protected @BindView(R.id.ap_listview) RecyclerView mListView;

    private Context mContext;
    private Handler mHandler;
    private List<AccessPoint> mAccessPoints = new ArrayList<>();
    private FastItemAdapter<AccessPoint> mFastAdapter = new FastItemAdapter<>();

    private Runnable wifiScan = new Runnable() {
        @Override
        @AfterPermissionGranted(RC_COARSE_LOCATION)
        public void run() {
            if(NetUtils.isOnWifi(mContext)) {
                if (isAdded()) {
                    ((MainActivity) getActivity()).updateFragment();
                }
            } else {
                getWifiManager(mContext.getApplicationContext()).startScan();

                if (EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    new io.manticore.android.util.WifiScanner(new Consumer<ScanResult>() {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull ScanResult result) throws Exception {
                            mHandler.removeCallbacks(wifiScan);

                            // Check if the result is a duplicate access point
                            for (int i = 0; i < mAccessPoints.size(); i++) {
                                if (mAccessPoints.get(i).matches(result.BSSID)) {
                                    mHandler.postDelayed(wifiScan, 6000);
                                    return;
                                }
                            }

                            mAccessPoints.add(new AccessPoint(result));
                            Collections.sort(mAccessPoints, new AccessPoint.LevelComparator());
                            mFastAdapter.setNewList(mAccessPoints);
                            mHandler.postDelayed(wifiScan, 6000);
                        }
                    }).start(mContext);
                } else {
                    EasyPermissions.requestPermissions((Activity) mContext, getString(R.string.permission_location_rationale), RC_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        mHandler = new Handler();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        if (!getWifiManager(mContext).isWifiEnabled()) getWifiManager(mContext).setWifiEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mFastAdapter);
        mListView.addItemDecoration(new DividerItemDecoration(mContext, 1));
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler.post(wifiScan);
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacks(wifiScan);
    }

    @Override
    public void onStop() {
        super.onStop();

        mHandler.removeCallbacks(wifiScan);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
