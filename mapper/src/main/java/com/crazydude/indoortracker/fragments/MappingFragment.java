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
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crazydude.indoortracker.R;
import com.crazydude.indoortracker.utils.WifiUtils;
import com.crazydude.indoortracker.views.MapperView;
import com.crazydude.indoortracker.views.WifiPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Crazy on 25.10.2016.
 */

public class MappingFragment extends Fragment implements View.OnClickListener, MapperView.WifiMapPointListener {

    private MapperView mMapperView;
    private Button mMapPointButton;
    private Integer mMapWidth, mMapHeight;
    private Set<WifiPoint> mWifiPoints = new HashSet<>();
    private WifiPoint mCurrentMappingPoint;
    private WifiManager mWifiManager;

    public static MappingFragment newInstance() {
        return new MappingFragment();
    }

    @Override
    public void onMapWifi(WifiPoint wifiPoint) {
        mWifiPoints.add(wifiPoint);
        mCurrentMappingPoint = wifiPoint;
        scanPoint();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapping, container, false);

        mMapperView = (MapperView) view.findViewById(R.id.mapper_view);
        mMapPointButton = (Button) view.findViewById(R.id.map_point_button);

        mMapPointButton.setOnClickListener(this);

        mMapperView.setWifiMapPointListener(this);

        if (savedInstanceState == null) {
            firstLaunchInit();
        } else {
            restoreMap();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_mapping, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mapping_save_data:
                if (mWifiPoints.size() <3) {
                    Toast.makeText(getContext(), R.string.map_three_points, Toast.LENGTH_SHORT).show();
                } else {
                    showMapNameDialog();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_point_button:
                mapPoint();
                break;
        }
    }

    private void saveData(String mapName) {
        try {
            WifiUtils.saveDataToFile(getContext(), mapName, mWifiPoints);
            Toast.makeText(getContext(), R.string.map_saved, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), String.format(getString(R.string.failed_to_save_map), e.getMessage()), Toast.LENGTH_LONG).show();
        }
    }

    private void scanPoint() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage(R.string.scanning)
                .show();
        mWifiManager.startScan();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                filterResults(scanResults);
                mCurrentMappingPoint.setScanResults(scanResults);
                mMapperView.update();
                alertDialog.dismiss();
                Toast.makeText(getContext(), R.string.scan_completed, Toast.LENGTH_SHORT).show();
                context.unregisterReceiver(this);
            }
        };

        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(receiver, intentFilter);
    }

    private void filterResults(List<ScanResult> scanResults) {
        List<ScanResult> newList = new ArrayList<>();
        for (ScanResult result : scanResults) {
            if (result.SSID.equals("Kappa")) {
                newList.add(result);
            }
        }

        scanResults.clear();
        scanResults.addAll(newList);
    }

    private void restoreMap() {
        mMapperView.createMap(mMapWidth, mMapHeight);
    }

    private void firstLaunchInit() {
//        showMapSizeDialog();
        createNewMap(6, 6); // for debug purpose only
    }

    private void showMapSizeDialog() {
        View inputDataView = View.inflate(getContext(), R.layout.dialog_map_size, null);
        EditText widthEditText = (EditText) inputDataView.findViewById(R.id.dialog_map_size_width_edit);
        EditText heightEditText = (EditText) inputDataView.findViewById(R.id.dialog_map_size_height_edit);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.enter_map_size)
                .setView(inputDataView)
                .setPositiveButton(R.string.create,
                        (dialogInterface, i) -> {
                            try {
                                createNewMap(Integer.valueOf(widthEditText.getText().toString()),
                                        Integer.valueOf(heightEditText.getText().toString()));
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), R.string.wrong_number, Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                                showMapSizeDialog();
                            }
                        }).show();
    }

    private void showMapNameDialog() {
        View inputDataView = View.inflate(getContext(), R.layout.dialog_map_name, null);
        EditText nameEditText = (EditText) inputDataView.findViewById(R.id.dialog_map_name_name_edit);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.enter_map_name)
                .setView(inputDataView)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton(R.string.create,
                        (dialogInterface, i) -> {
                            if (TextUtils.isEmpty(nameEditText.getText())) {
                                showMapNameDialog();
                                Toast.makeText(getContext(), R.string.name_cannot_be_empty, Toast.LENGTH_SHORT).show();
                            } else {
                                saveData(nameEditText.getText().toString());
                            }
                        }).show();
    }

    private void createNewMap(int width, int height) {
        mMapWidth = width;
        mMapHeight = height;
        mMapperView.createMap(width, height);
    }

    private void mapPoint() {
        mMapperView.switchMode(MapperView.Mode.MAP);
    }
}
