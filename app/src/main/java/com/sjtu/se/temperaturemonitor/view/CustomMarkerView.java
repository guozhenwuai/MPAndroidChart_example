package com.sjtu.se.temperaturemonitor.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.sjtu.se.temperaturemonitor.R;

import java.text.DecimalFormat;

/**
 * Created by xinywu on 2017/11/2.
 */

public class CustomMarkerView extends MarkerView {
    private TextView tvContent;
    private DecimalFormat mFormat;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        mFormat = new DecimalFormat("0.0");
        tvContent = (TextView) findViewById(R.id.markerContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String s = mFormat.format(e.getY());
        if((int)e.getData() == 0){
            tvContent.setText(s +"%RH");
        }
        else{
            tvContent.setText(s + "℃");
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
