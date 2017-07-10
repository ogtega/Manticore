package io.manticore.android.fragment;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import io.manticore.android.MainActivity;
import io.manticore.android.util.NetUtils;

public class NetworkScanner extends Fragment {

    private Context mContext;
    private Handler mHandler = new Handler();

    private final String TAG = "NetworkScanner";

    private Runnable networkScan = new Runnable() {
        @Override
        public void run() {
            if(!NetUtils.isOnWifi(mContext)) {
                if (isAdded()) {
                    ((MainActivity) getActivity()).updateFragment();
                }
            } else {
                // TODO: open them sockets ;)
                mHandler.postDelayed(networkScan, 6000);
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
        Log.i(TAG, "NetworkScanner");
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler.post(networkScan);
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacks(networkScan);
    }

    @Override
    public void onStop() {
        super.onStop();

        mHandler.removeCallbacks(networkScan);
    }
}
