package com.crazydude.indoortracker.algorithms;

import android.net.wifi.ScanResult;

import com.crazydude.indoortracker.models.Position;

import java.util.List;

/**
 * Created by Crazy on 15.11.2016.
 */

public class RSSIPositionDetector implements PositionDetector {

    private List<ScanResult> mScanResults;

    public RSSIPositionDetector(List<ScanResult> scanResults) {
        mScanResults = scanResults;
    }

    @Override
    public Position detectPosition() {
        return new Position(3, 3);
    }
}
