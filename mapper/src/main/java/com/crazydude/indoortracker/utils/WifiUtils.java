package com.crazydude.indoortracker.utils;

/**
 * Created by Crazy on 02.11.2016.
 */

public class WifiUtils {

    private static final int MIN_RSSI = -100;
    private static final int MAX_RSSI = -30;

    /**
     * This method copied from android WifiManager. I have changed MAX_RSSI constant value so I will get
     * more precise signal level value at top range
     * @param rssi Signal strength in dBm
     * @param numLevels Maximum for the return value
     * @return Quality of the signal. May not be greater than numLevels
     */
    public static int calculateSignalLevel(int rssi, int numLevels) {
        if (rssi <= MIN_RSSI) {
            return 0;
        } else if (rssi >= MAX_RSSI) {
            return numLevels - 1;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (numLevels - 1);
            return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
        }
    }
}
