package io.manticore.android.model;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;

public class AccessPoint extends AbstractItem<AccessPoint, AccessPoint.ViewHolder> {

    private String ESSID;
    private String BSSID;

    private int level;
    private int channel;
    private int imageLevel;
    private double frequency;
    private DecimalFormat df = new DecimalFormat("#.0");

    private String vendor;
    private boolean secure;
    private String protocol = "None";

    private static final String[] protocols = {"EAP", "WPA", "WEP"};

    public AccessPoint(ScanResult result) {

        ESSID = result.SSID;
        BSSID = result.BSSID;

        level = result.level;
        channel = getChannel(result.frequency);
        imageLevel = WifiManager.calculateSignalLevel(result.level, 4);

        for (String protocol : protocols) {
            if (result.capabilities.contains(protocol)) {

                secure = true;
                this.protocol = (protocol.equals("WPA")) ? "WPA/WPA2 PSK" : protocol;
                break;
            }
        }

        vendor = "";

        frequency = ((((result.frequency / 1000.0) * 10.0) / 10.0));
    }

    @Override
    public int getType() {
        return R.id.ap_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.ap_list_item;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.name.setText(String.format("%s%s", ESSID, vendor));
        holder.radio.setText(level + "db (" + df.format(frequency) + " Ghz) " + channel);
        holder.protocol.setText(protocol);
        holder.signal.setImageState(secure ? new int[]{R.attr.state_encrypted} : new int[]{}, true);
        holder.signal.getDrawable().setLevel(imageLevel);
        Log.i("AP", ESSID + ' ' + BSSID + ' ' + imageLevel + ':' + holder.signal.getDrawable().getLevel());
        holder.details.setText(BSSID );
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ap_item_name) TextView name;
        @BindView(R.id.ap_item_radio) TextView radio;
        @BindView(R.id.ap_item_details) TextView details;
        @BindView(R.id.ap_item_protocol) TextView protocol;
        @BindView(R.id.wifi_signal) ImageView signal;

        ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    private static int getChannel(int freq) {

        if (freq == 2484) {
            return 14;
        }

        if (freq < 2484) {
            return (freq - 2407) / 5;
        }

        return freq / 5 - 1000;
    }

    // Compare AccessPoints by their MAC Address
    public boolean matches(AccessPoint ap) {
        return (this.BSSID).equals(ap.BSSID);
    }

    public static class LevelComparator implements Comparator<AccessPoint> {

        @Override
        public int compare(AccessPoint ap, AccessPoint _ap) {
            // Sort by signal strength
            int difference = WifiManager.compareSignalLevel(_ap.level, ap.level);

            if (difference != 0) {
                return difference;
            }
            // Sort by ESSIDs
            difference = ap.ESSID.compareToIgnoreCase(_ap.ESSID);

            if (difference !=0) {
                return difference;
            }

            // Sort by BSSIDs
            return ap.BSSID.compareToIgnoreCase(_ap.BSSID);
        }
    }

    @Override
    public View createView(Context ctx, @Nullable ViewGroup parent) {
        return super.createView(ctx, parent);
    }
}
