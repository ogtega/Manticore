package io.manticore.android.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.ButterKnife;
import io.manticore.android.R;

public class NetworkHost extends AbstractItem<NetworkHost, NetworkHost.ViewHolder> {

    private int ping;
    private boolean online;
    private String address;
    private String hostname;

    public NetworkHost(String address, String hostname, int ping, boolean online) {
        this.address = address;
        this.online = online;
        this.hostname = hostname;
        this.ping = ping;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);


    }

    public boolean matches(NetworkHost host) {
        return this.address.equals(host.address);
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View createView(Context ctx, @Nullable ViewGroup parent) {
        return super.createView(ctx, parent);
    }
}
