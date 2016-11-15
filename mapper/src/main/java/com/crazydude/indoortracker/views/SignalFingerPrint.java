package com.crazydude.indoortracker.views;

import android.net.wifi.ScanResult;

import com.crazydude.indoortracker.models.Position;

import java.util.List;

/**
 * Created by Crazy on 27.10.2016.
 */
public class SignalFingerPrint {

    private Position position;
    private List<ScanResult> scanResults;

    public SignalFingerPrint(float x, float y) {
        this.position = new Position(x, y);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }
}
