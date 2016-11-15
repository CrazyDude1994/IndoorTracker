package com.crazydude.indoortracker.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.crazydude.indoortracker.R;
import com.crazydude.indoortracker.algorithms.SignalLevelPointDetector;
import com.crazydude.indoortracker.algorithms.WifiPointDetectorAlgorithm;
import com.crazydude.indoortracker.models.MapFileModel;
import com.crazydude.indoortracker.models.WifiPoint;
import com.crazydude.indoortracker.utils.WifiUtils;
import com.crazydude.indoortracker.views.MapperView;
import com.crazydude.indoortracker.views.SignalFingerPrint;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Crazy on 25.10.2016.
 */

public class MappingFragment extends Fragment implements MapperView.WifiMapPointListener {

    private MapperView mMapperView;
    private Integer mMapWidth, mMapHeight;
    private Set<SignalFingerPrint> mSignalFingerPrints = new HashSet<>();
    private SignalFingerPrint mCurrentMappingPoint;
    private WifiManager mWifiManager;
    private Snackbar mModeSnackbar;
    private MenuItem mMappingModeItem;
    private WifiPointDetectorAlgorithm mWifiPointDetectorAlgorithm;

    public static MappingFragment newInstance() {
        return new MappingFragment();
    }

    public static MappingFragment newInstance(MapFileModel fileModel) {
        MappingFragment mappingFragment = new MappingFragment();
        mappingFragment.setData(fileModel);
        return mappingFragment;
    }

    public void setData(MapFileModel data) {
        mSignalFingerPrints = data.getSignalFingerPrints();
        mMapWidth = data.getRoomWidth();
        mMapHeight = data.getRoomHeight();
    }

    @Override
    public void onMapWifi(SignalFingerPrint signalFingerPrint) {
        mSignalFingerPrints.add(signalFingerPrint);
        mCurrentMappingPoint = signalFingerPrint;
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
                if (mSignalFingerPrints.size() < 3) {
                    Toast.makeText(getContext(), R.string.map_three_points, Toast.LENGTH_SHORT).show();
                } else {
                    showMapNameDialog();
                }
                return true;
            case R.id.map_point_button:
                mMappingModeItem = item;
                switchToMappingMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveData(String mapName) {
        try {
            WifiUtils.saveDataToFile(getContext(), mapName, mSignalFingerPrints, mMapWidth, mMapHeight);
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
                mCurrentMappingPoint.setScanResults(scanResults);
                mMapperView.update();
                alertDialog.dismiss();
                Toast.makeText(getContext(), R.string.scan_completed, Toast.LENGTH_SHORT).show();
//                calculateWifiPointPositions();
                context.unregisterReceiver(this);
            }
        };

        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(receiver, intentFilter);
    }

    private void calculateWifiPointPositions() {
        if (mSignalFingerPrints.size() < 3) {
            return;
        }
        mWifiPointDetectorAlgorithm = new SignalLevelPointDetector(mSignalFingerPrints);
        Set<WifiPoint> wifiPoints = mWifiPointDetectorAlgorithm.detectWifiPointPosition();
        mMapperView.setWifiPoints(wifiPoints);
    }

    private void restoreMap() {
        mMapperView.createMap(mMapWidth, mMapHeight);
    }

    private void firstLaunchInit() {
        if (mMapHeight == null) {
//                    showMapSizeDialog();
            createNewMap(6, 6); // for debug purpose only
        } else {
            createNewMap(mMapWidth, mMapWidth);
            mMapperView.setSignalFingerPrints(mSignalFingerPrints);
//            calculateWifiPointPositions();
        }
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

    private void switchToMappingMode() {
        mMapperView.switchMode(MapperView.Mode.MAP);
        mMappingModeItem.setVisible(false);
        mModeSnackbar = Snackbar.make(getView(), R.string.mapping_mode, Snackbar.LENGTH_INDEFINITE);
        mModeSnackbar.setAction(R.string.switch_to_view_mode,
                view -> {
                    mMapperView.switchMode(MapperView.Mode.VIEW);
                    mModeSnackbar.dismiss();
                    mMappingModeItem.setVisible(true);
                });
        mModeSnackbar.show();
    }
}
