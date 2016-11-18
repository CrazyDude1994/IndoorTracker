package com.crazydude.indoortracker.models;

import com.crazydude.indoortracker.views.SignalFingerPrint;
import com.google.gson.annotations.SerializedName;

import java.util.Set;

/**
 * Created by Crazy on 07.11.2016.
 */

public class MapFileModel {

    @SerializedName("scan_results")
    private Set<SignalFingerPrint> mSignalFingerPrints;
    @SerializedName("filtered_bssid")
    private Set<String> mFilteredBSSIDS;
    @SerializedName("map_name")
    private String mMapName;
    @SerializedName("width")
    private int mRoomWidth;
    @SerializedName("height")
    private int mRoomHeight;

    public MapFileModel(Set<SignalFingerPrint> signalFingerPrints, Set<String> filteredBSSIDS, String mapName, int roomWidth, int roomHeight) {
        mSignalFingerPrints = signalFingerPrints;
        mFilteredBSSIDS = filteredBSSIDS;
        mMapName = mapName;
        mRoomWidth = roomWidth;
        mRoomHeight = roomHeight;
    }

    public int getRoomHeight() {
        return mRoomHeight;
    }

    public void setRoomHeight(int roomHeight) {
        mRoomHeight = roomHeight;
    }

    public int getRoomWidth() {
        return mRoomWidth;
    }

    public void setRoomWidth(int roomWidth) {
        mRoomWidth = roomWidth;
    }

    public Set<SignalFingerPrint> getSignalFingerPrints() {
        return mSignalFingerPrints;
    }

    public void setSignalFingerPrints(Set<SignalFingerPrint> signalFingerPrints) {
        mSignalFingerPrints = signalFingerPrints;
    }

    public String getMapName() {
        return mMapName;
    }

    public void setMapName(String mapName) {
        mMapName = mapName;
    }

    public Set<String> getFilteredBSSIDS() {
        return mFilteredBSSIDS;
    }

    public void setFilteredBSSIDS(Set<String> filteredBSSIDS) {
        mFilteredBSSIDS = filteredBSSIDS;
    }
}
