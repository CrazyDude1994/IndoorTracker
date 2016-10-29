package com.crazydude.indoortracker.views;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by Crazy on 27.10.2016.
 */
public class WifiPoint {

    private float x;
    private float y;
    private List<ScanResult> mScanResult;

    public WifiPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public List<ScanResult> getScanResult() {
        return mScanResult;
    }

    public void setScanResult(List<ScanResult> scanResult) {
        mScanResult = scanResult;
    }
}
