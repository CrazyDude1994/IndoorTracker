package com.crazydude.indoortracker.views;

import android.net.wifi.ScanResult;

import com.crazydude.indoortracker.models.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Crazy on 27.10.2016.
 */
public class SignalFingerPrint {

    private Position position;
    private List<ScanResult> scanResults = new ArrayList<>();

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

    public void addScanResults(List<ScanResult> scanResults) {
        this.scanResults.addAll(scanResults);
    }
}
