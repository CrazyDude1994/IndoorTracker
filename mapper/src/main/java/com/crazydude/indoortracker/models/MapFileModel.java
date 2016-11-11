package com.crazydude.indoortracker.models;

import com.crazydude.indoortracker.views.WifiPoint;
import com.google.gson.annotations.SerializedName;

import java.util.Set;

/**
 * Created by Crazy on 07.11.2016.
 */

public class MapFileModel {

    @SerializedName("scan_results")
    private Set<WifiPoint> mWifiPoints;
    @SerializedName("map_name")
    private String mMapName;
    @SerializedName("width")
    private int mRoomWidth;
    @SerializedName("height")
    private int mRoomHeight;

    public MapFileModel(Set<WifiPoint> wifiPoints, String mapName, int roomWidth, int roomHeight) {
        mWifiPoints = wifiPoints;
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

    public Set<WifiPoint> getWifiPoints() {
        return mWifiPoints;
    }

    public void setWifiPoints(Set<WifiPoint> wifiPoints) {
        mWifiPoints = wifiPoints;
    }

    public String getMapName() {
        return mMapName;
    }

    public void setMapName(String mapName) {
        mMapName = mapName;
    }
}
