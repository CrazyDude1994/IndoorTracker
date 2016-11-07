package com.crazydude.indoortracker.adapters;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Crazy on 22.10.2016.
 */

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    private List<ScanResult> mScanResultList = new ArrayList<>();

    public void setData(List<ScanResult> data) {
        mScanResultList = data;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new TextView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanResult result = mScanResultList.get(position);
        ((TextView) holder.itemView).setText(String.format("%s %d", result.SSID, result.level));
    }

    @Override
    public int getItemCount() {
        return mScanResultList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
