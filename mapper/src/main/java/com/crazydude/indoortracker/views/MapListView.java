package com.crazydude.indoortracker.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.crazydude.indoortracker.R;
import com.crazydude.indoortracker.models.MapFileModel;

/**
 * Created by Crazy on 07.11.2016.
 */

public class MapListView extends TextView {

    public MapListView(Context context) {
        super(context);
        init();
    }

    public MapListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MapListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setData(MapFileModel data) {
        setText(String.format(getContext().getString(R.string.map_list_data_format), data.getMapName(), data.getSignalFingerPrints().size()));
    }

    private void init() {
//        inflate(getContext(), R.layout.view_map_list_item, null);
        setPadding(0, 24, 0, 24);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }
}
