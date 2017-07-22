package io.manticore.android.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;

public class NetworkHost extends AbstractItem<NetworkHost, NetworkHost.ViewHolder> {

    private String hostname;

    public NetworkHost(String hostname) {
        this.hostname = hostname;
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
        holder.hostname.setText(hostname);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.net_item_hostname) TextView hostname;

        ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}
