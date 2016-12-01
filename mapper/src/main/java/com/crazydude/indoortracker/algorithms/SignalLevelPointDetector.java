package com.crazydude.indoortracker.algorithms;

import android.net.wifi.ScanResult;

import com.crazydude.indoortracker.models.Position;
import com.crazydude.indoortracker.models.WifiPoint;
import com.crazydude.indoortracker.views.SignalFingerPrint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Crazy on 11.11.2016.
 */

public class SignalLevelPointDetector implements WifiPointDetectorAlgorithm {

    private Set<SignalFingerPrint> mFingerPrints;
    private Map<WifiPoint, Integer> mRSSIData;

    public SignalLevelPointDetector(Set<SignalFingerPrint> fingerPrints) {
        mFingerPrints = fingerPrints;
        mapFingerPrints();
    }

    @Override
    public Set<WifiPoint> detectWifiPointPosition() {
        for (SignalFingerPrint fingerPrint : mFingerPrints) {
            fingerPrint.
        }

        return wifiPoints;
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

    private class FingerPrint {

        private Position mPosition;
        private int mSignalLevel;

        public FingerPrint(Position position, int signalLevel) {
            mPosition = position;
            mSignalLevel = signalLevel;
        }

        public Position getPosition() {
            return mPosition;
        }

        public void setPosition(Position position) {
            mPosition = position;
        }

        public int getSignalLevel() {
            return mSignalLevel;
        }

        public void setSignalLevel(int signalLevel) {
            mSignalLevel = signalLevel;
        }
    }
}
