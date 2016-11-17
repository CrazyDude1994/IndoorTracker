package com.crazydude.indoortracker.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazydude.indoortracker.R;
import com.crazydude.indoortracker.algorithms.PositionDetector;
import com.crazydude.indoortracker.algorithms.RSSIPositionDetector;
import com.crazydude.indoortracker.models.MapFileModel;
import com.crazydude.indoortracker.models.Position;
import com.crazydude.indoortracker.views.MapperView;
import com.crazydude.indoortracker.views.SignalFingerPrint;

import java.util.List;
import java.util.Set;

/**
 * Created by Crazy on 15.11.2016.
 */

public class NavigationFragment extends Fragment {

    private MapperView mMapperView;
    private Set<SignalFingerPrint> mSignalFingerPrints;
    private int mMapWidth;
    private int mMapHeight;
    private WifiManager mWifiManager;
    private BroadcastReceiver mReceiver;
    private PositionDetector mPositionDetector;

    public static NavigationFragment newInstance(MapFileModel fileModel) {
        NavigationFragment navigationFragment = new NavigationFragment();
        navigationFragment.setData(fileModel);
        return navigationFragment;
    }

    public void setData(MapFileModel data) {
        mSignalFingerPrints = data.getSignalFingerPrints();
        mMapWidth = data.getRoomWidth();
        mMapHeight = data.getRoomHeight();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        mMapperView = (MapperView) view.findViewById(R.id.mapper_view);

        initMap();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        scanPoint();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScan();
    }

    private void initMap() {
        mMapperView.createMap(mMapWidth, mMapHeight);
        mMapperView.setSignalFingerPrints(mSignalFingerPrints);
    }

    private void scanPoint() {
        mWifiManager.startScan();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getUserPosition();
                mWifiManager.startScan();
            }
        };

        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    private void getUserPosition() {
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        mPositionDetector = new RSSIPositionDetector(scanResults, mSignalFingerPrints);
        Position position = mPositionDetector.detectPosition();
        mMapperView.setUserPosition(position);
    }

    private void stopScan() {
        getContext().unregisterReceiver(mReceiver);
    }
}
