package com.crazydude.indoortracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.crazydude.indoortracker.models.MapFileModel;
import com.crazydude.indoortracker.views.MapListView;

import java.util.List;

/**
 * Created by Crazy on 07.11.2016.
 */

public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.ViewHolder> {

    private final List<MapFileModel> mData;
    private MapListAdapterClickListener mMapListAdapterClickListener;

    public MapListAdapter(List<MapFileModel> data) {
        this.mData = data;
    }

    public void setMapListAdapterClickListener(MapListAdapterClickListener mapListAdapterClickListener) {
        mMapListAdapterClickListener = mapListAdapterClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new MapListView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MapListView itemView = (MapListView) holder.itemView;
        itemView.setData(mData.get(position));
        itemView.setOnClickListener(new MapListClickListener(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface MapListAdapterClickListener {
        void onClick(MapFileModel fileModel);
    }

    private class MapListClickListener implements View.OnClickListener {

        private int mPosition;

        public MapListClickListener(int position) {
            mPosition = position;
        }

        public int getPosition() {
            return mPosition;
        }

        @Override
        public void onClick(View view) {
            if (MapListAdapter.this.mMapListAdapterClickListener != null) {
                MapFileModel model = MapListAdapter.this.mData.get(mPosition);
                mMapListAdapterClickListener.onClick(model);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
