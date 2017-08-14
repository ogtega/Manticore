package io.manticore.android.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;

public class NetworkHost extends AbstractItem<NetworkHost, NetworkHost.ViewHolder> {

    private int host;
    private String ip;
    private String mac;
    private String vendor;
    private String hostname;

    private boolean online;

    public NetworkHost(String ip) {
        this.ip = ip;
    }

    public NetworkHost(int host, String ip, String hostname, String mac, String vendor) {
        this.host = host;
        this.mac = mac;
        this.online = true;
        this.ip = ip;
        this.hostname = hostname;
        this.vendor = vendor;
    }

    int getHost() {
        return host;
    }

    public String getMac() {
        return mac;
    }

    public String getIp() {
        return ip;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public boolean isOnline() {
        return online;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.net_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.net_list_item;
    }

    @Override
    public View createView(Context ctx, @Nullable ViewGroup parent) {
        return super.createView(ctx, parent);
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.ip.setText(ip);
        holder.mac.setText(mac);
        holder.vendor.setText(vendor);
        holder.name.setText(hostname);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.net_item_ip) AppCompatTextView ip;
        @BindView(R.id.net_item_mac) AppCompatTextView mac;
        @BindView(R.id.net_item_name) AppCompatTextView name;
        @BindView(R.id.net_item_details) AppCompatTextView vendor;

        ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public static class HostComparator implements Comparator<NetworkHost> {

        @Override
        public int compare(NetworkHost host, NetworkHost t1) {
            return host.getHost() - (t1.getHost());
        }
    }
}
