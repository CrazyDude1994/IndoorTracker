package com.crazydude.indoortracker.utils;

import android.content.Context;

import com.crazydude.indoortracker.models.MapFileModel;
import com.crazydude.indoortracker.views.SignalFingerPrint;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void saveDataToFile(Context context, String mapName, Set<SignalFingerPrint> data, int width,
                                      int height) throws IOException {
        Gson gson = buildGson();

        File file = new File(context.getFilesDir(), mapName + ".json");
        MapFileModel fileModel = new MapFileModel(data, mapName, width, height);
        String json = gson.toJson(fileModel);
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] bytes = json.getBytes();
        outputStream.write(bytes);
        outputStream.close();
    }

    public static List<MapFileModel> loadMapList(Context context) {
        Gson gson = buildGson();
        List<MapFileModel> result = new ArrayList<>();

        File filesDir = context.getFilesDir();
        for (File file : filesDir.listFiles(
                (file1, s) -> {
                    return s.endsWith(".json");
                })
                ) {
            try {
                String data = FileUtils.readFile(file);
                MapFileModel fileModel = gson.fromJson(data, MapFileModel.class);

                result.add(new MapFileModel(fileModel.getSignalFingerPrints(), file.getName(),
                        fileModel.getRoomWidth(), fileModel.getRoomHeight()));
            } catch (IOException e) {
                continue;
            }
        }

        return result;
    }

    private static Gson buildGson() {
        List<String> exludedList = Arrays.asList("operatorFriendlyName", "venueName", "informationElements", "wifiSsid");
        return new GsonBuilder()
                .setPrettyPrinting()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return exludedList.contains(f.getName());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .disableHtmlEscaping()
                .create();
    }
}
