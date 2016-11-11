package com.crazydude.indoortracker.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.crazydude.indoortracker.R;
import com.crazydude.indoortracker.adapters.MapListAdapter;
import com.crazydude.indoortracker.fragments.MappingFragment;
import com.crazydude.indoortracker.models.MapFileModel;
import com.crazydude.indoortracker.utils.WifiUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MapListAdapter.MapListAdapterClickListener {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private AlertDialog mMapChooseDialog;

    @Override
    public void onClick(MapFileModel fileModel) {
        navigationMapLoadClick(fileModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        init();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_navigation_mapping:
                navigationMappingClick();
                break;
            case R.id.menu_navigation_load_map:
                navigationMapLoadClick();
                break;
        }

        mDrawerLayout.closeDrawers(); // close navigation drawer
        return true;
    }

    private void navigationMapLoadClick(MapFileModel fileModel) {
        if (mMapChooseDialog != null) {
            mMapChooseDialog.dismiss();
        }
        switchMainContainer(MappingFragment.newInstance(fileModel));
    }

    private void init() {
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void navigationMapLoadClick() {
        showMapChooseDialog();
    }

    private void showMapChooseDialog() {
        List<MapFileModel> mapList = WifiUtils.loadMapList(this);

        View inputDataView = View.inflate(this, R.layout.dialog_map_list, null);
        RecyclerView recyclerView = (RecyclerView) inputDataView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MapListAdapter mapListAdapter = new MapListAdapter(mapList);
        mapListAdapter.setMapListAdapterClickListener(this);
        recyclerView.setAdapter(mapListAdapter);

        mMapChooseDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.choose_map)
                .setView(inputDataView)
                .setCancelable(true)
                .show();
    }

    private void navigationMappingClick() {
        switchMainContainer(MappingFragment.newInstance());
    }

    private void switchMainContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }
}
