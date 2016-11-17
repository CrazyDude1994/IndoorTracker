package com.crazydude.indoortracker.algorithms;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.crazydude.indoortracker.models.Position;
import com.crazydude.indoortracker.models.WifiPoint;
import com.crazydude.indoortracker.utils.WifiUtils;
import com.crazydude.indoortracker.views.SignalFingerPrint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Crazy on 15.11.2016.
 */

public class RSSIPositionDetector implements PositionDetector {

    private List<ScanResult> mScanResults;
    private Set<SignalFingerPrint> mFingerPrints;
    private Map<WifiPoint, Integer> mRSSIData;

    public RSSIPositionDetector(List<ScanResult> scanResults, Set<SignalFingerPrint> fingerPrints) {
        mScanResults = scanResults;
        mFingerPrints = fingerPrints;

        mapFingerPrints();
    }

    @Override
    public Position detectPosition() {
        return calculateSignalLevels();
    }

    private Position calculateSignalLevels() {
        Position position = null;
        Float distance = null;

        for (SignalFingerPrint fingerPrint : mFingerPrints) {
            float tempDistance = 0f;
            for (ScanResult scanResult : mScanResults) {
                WifiPoint wifiPoint = new WifiPoint(scanResult.SSID, scanResult.BSSID, fingerPrint.getPosition());
                Integer avrg = mRSSIData.get(wifiPoint);
                if (avrg != null) {
                    int level = WifiUtils.calculateSignalLevel(avrg, 100);
                    int scanLevel = WifiUtils.calculateSignalLevel(scanResult.level, 100);

                    tempDistance = tempDistance + (float) Math.pow((level - scanLevel), 2);
                }
            }

            tempDistance = (float) Math.sqrt(tempDistance);


            if (distance != null) {
                if (tempDistance < distance) {
                    distance = tempDistance;
                    position = fingerPrint.getPosition();
                    Log.d("RSSI", String.format("%f < %f", tempDistance, distance));
                }
            } else {
                Log.d("RSSI", String.format("Distaince is null. Choosing: %f,%f: %f", fingerPrint.getPosition().getX(),
                        fingerPrint.getPosition().getY(), tempDistance));
                position = fingerPrint.getPosition();
                distance = tempDistance;
            }
        }

        return position;
    }

    private void mapFingerPrints() {
        mRSSIData = new HashMap<>();
        HashMap<WifiPoint, Integer> mNumberCount = new HashMap<>();

        for (SignalFingerPrint fingerPrint : mFingerPrints) {
            for (ScanResult result : fingerPrint.getScanResults()) {
                WifiPoint wifiPoint = new WifiPoint(result.SSID, result.BSSID, fingerPrint.getPosition());
                Integer signalLevel = mRSSIData.get(wifiPoint);
                if (signalLevel == null) {
                    signalLevel = result.level;
                    mNumberCount.put(wifiPoint, 1);
                } else {
                    signalLevel += result.level;
                    mNumberCount.put(wifiPoint, mNumberCount.get(wifiPoint) + 1);
                }

                mRSSIData.put(wifiPoint, signalLevel);
            }
        }

        for (WifiPoint wifiPoint : mRSSIData.keySet()) {
            Integer signalLevel = mRSSIData.get(wifiPoint);
            signalLevel = signalLevel / mNumberCount.get(wifiPoint);
            mRSSIData.put(wifiPoint, signalLevel);
        }
    }
}
