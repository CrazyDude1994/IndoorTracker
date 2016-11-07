package com.crazydude.indoortracker.utils;

import android.content.Context;
import android.net.wifi.ScanResult;

import com.crazydude.indoortracker.models.MapFileModel;
import com.crazydude.indoortracker.views.WifiPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Crazy on 02.11.2016.
 */

public class WifiUtils {

    private static final int MIN_RSSI = -100;
    private static final int MAX_RSSI = -30;

    /**
     * This method copied from android WifiManager. I have changed MAX_RSSI constant value so I will get
     * more precise signal level value at top range
     *
     * @param rssi      Signal strength in dBm
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
            return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
        }
    }

    public static void saveDataToFile(Context context, String mapName, Set<WifiPoint> data) throws IOException {
        Gson gson = buildGson();

        File file = new File(context.getFilesDir(), mapName + ".json");
        String json = gson.toJson(data);
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] bytes = json.getBytes();
        outputStream.write(bytes);
        outputStream.close();
    }

    public static List<MapFileModel> loadMapList(Context context) {
        Gson gson = buildGson();
        List<MapFileModel> result = new ArrayList<>();

        File filesDir = context.getFilesDir();
        for (File file : filesDir.listFiles()) {
            try {
                String data = FileUtils.readFile(file);
                ScanResult[] scanResults = gson.fromJson(data, ScanResult[].class);
                List<ScanResult> resultList = new ArrayList<>(Arrays.asList(scanResults));

                result.add(new MapFileModel(file.getName(), new HashSet<>(resultList)));
            } catch (IOException e) {
                continue;
            }
        }

        return result;
    }

    private static Gson buildGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }
}
