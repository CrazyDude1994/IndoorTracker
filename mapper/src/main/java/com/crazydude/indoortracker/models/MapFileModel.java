package com.crazydude.indoortracker.models;

import android.net.wifi.ScanResult;

import java.util.Set;

/**
 * Created by Crazy on 07.11.2016.
 */

public class MapFileModel {

    private Set<ScanResult> mScanResults;
    private String mMapName;

    public MapFileModel(String mapName, Set<ScanResult> scanResults) {
        mScanResults = scanResults;
        mMapName = mapName;
    }

    public Set<ScanResult> getScanResults() {
        return mScanResults;
    }

    public void setScanResults(Set<ScanResult> scanResults) {
        mScanResults = scanResults;
    }

    public String getMapName() {
        return mMapName;
    }

    public void setMapName(String mapName) {
        mMapName = mapName;
    }
}
