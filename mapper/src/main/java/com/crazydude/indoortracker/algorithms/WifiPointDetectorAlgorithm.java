package com.crazydude.indoortracker.algorithms;

import com.crazydude.indoortracker.models.WifiPoint;

import java.util.Set;

/**
 * Created by Crazy on 11.11.2016.
 */

public interface WifiPointDetectorAlgorithm {

    Set<WifiPoint> detectWifiPointPosition();
}
