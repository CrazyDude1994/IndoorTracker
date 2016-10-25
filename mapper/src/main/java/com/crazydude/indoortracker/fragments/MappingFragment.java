package com.crazydude.indoortracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.crazydude.indoortracker.R;

/**
 * Created by Crazy on 25.10.2016.
 */

public class MappingFragment extends Fragment {

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

        if (savedInstanceState == null) {
            firstLaunchInit();
        }

        return view;
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

    }
}
