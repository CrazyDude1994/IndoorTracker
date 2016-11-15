package com.crazydude.indoortracker.algorithms;

import android.net.wifi.ScanResult;

import com.crazydude.indoortracker.models.Position;
import com.crazydude.indoortracker.models.WifiPoint;
import com.crazydude.indoortracker.utils.WifiUtils;
import com.crazydude.indoortracker.views.SignalFingerPrint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mikera.vectorz.Vector2;

/**
 * Created by Crazy on 11.11.2016.
 */

public class SignalLevelPointDetector implements WifiPointDetectorAlgorithm {

    private Set<SignalFingerPrint> mFingerPrints;

    public SignalLevelPointDetector(Set<SignalFingerPrint> fingerPrints) {
        mFingerPrints = fingerPrints;
    }

    @Override
    public Set<WifiPoint> detectWifiPointPosition() {
        Set<WifiPoint> wifiPoints = new HashSet<>();
        Map<String, List<FingerPrint>> positionData = new HashMap<>();
        for (SignalFingerPrint fingerPrint : mFingerPrints) {
            for (ScanResult scanResult : fingerPrint.getScanResults()) {
                List<FingerPrint> positions = positionData.get(scanResult.SSID);
                FingerPrint data = new FingerPrint(fingerPrint.getPosition(), WifiUtils.calculateSignalLevel(scanResult.level, 100));
                if (positions == null) {
                    positions = new ArrayList<>(Collections.singleton(data));
                } else {
                    positions.add(data);
                }

                positionData.put(scanResult.SSID, positions);
            }
        }

        FingerPrint t1 = positionData.get("Kappa").get(0);
        FingerPrint t2 = positionData.get("Kappa").get(1);
        FingerPrint t3 = positionData.get("Kappa").get(2);

        Vector2 vector1 = Vector2.of(t1.getPosition().getX(), t1.getPosition().getY());
        Vector2 vector2 = Vector2.of(t2.getPosition().getX(), t2.getPosition().getY());
        Vector2 vector3 = Vector2.of(t3.getPosition().getX(), t3.getPosition().getY());

        int s1 = 100 - t3.getSignalLevel();
        int s2 = 100 - t2.getSignalLevel();
        int sumS = s1 + s2;

        double distance = vector3.subCopy(vector2).magnitude();


        return wifiPoints;
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
