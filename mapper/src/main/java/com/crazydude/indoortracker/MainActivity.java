package com.crazydude.indoortracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private WifiManager mWifiManager;
    private WifiAdapter mWifiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        init();
    }

    private void init() {
        mWifiAdapter = new WifiAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mWifiAdapter);
        mWifiManager.startScan();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mWifiManager != null) {
                    mWifiManager.startScan();
                    handler.postDelayed(this, 3000);
                }
            }
        };

        handler.postDelayed(runnable, 3000);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mWifiAdapter != null) {
                    mWifiAdapter.setData(mWifiManager.getScanResults());
                } else {
                    unregisterReceiver(this);
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
}
