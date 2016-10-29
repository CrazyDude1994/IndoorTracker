package com.crazydude.indoortracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.crazydude.indoortracker.R;
import com.crazydude.indoortracker.views.MapperView;

/**
 * Created by Crazy on 25.10.2016.
 */

public class MappingFragment extends Fragment implements View.OnClickListener {

    private MapperView mMapperView;
    private Button mMapPointButton;
    private Integer mMapWidth, mMapHeight;

    public static MappingFragment newInstance() {
        return new MappingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapping, container, false);

        mMapperView = (MapperView) view.findViewById(R.id.mapper_view);
        mMapPointButton = (Button) view.findViewById(R.id.map_point_button);

        mMapPointButton.setOnClickListener(this);

        if (savedInstanceState == null) {
            firstLaunchInit();
        } else {
            restoreMap();
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_point_button:
                mapPoint();
                break;
        }
    }

    private void restoreMap() {
        mMapperView.createMap(mMapWidth, mMapHeight);
    }

    private void firstLaunchInit() {
        showMapSizeDialog();
    }

    private void showMapSizeDialog() {
        View inputDataView = View.inflate(getContext(), R.layout.dialog_map_size, null);
        EditText widthEditText = (EditText) inputDataView.findViewById(R.id.dialog_map_size_width_edit);
        EditText heightEditText = (EditText) inputDataView.findViewById(R.id.dialog_map_size_height_edit);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.enter_map_size)
                .setView(inputDataView)
                .setPositiveButton(R.string.create,
                        (dialogInterface, i) -> {
                            try {
                                createNewMap(Integer.valueOf(widthEditText.getText().toString()),
                                        Integer.valueOf(heightEditText.getText().toString()));
                            } catch (NumberFormatException e) {
                                dialogInterface.dismiss();
                                showMapSizeDialog();
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
