package com.sjtu.se.temperaturemonitor.formater;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * Created by xinywu on 2017/11/3.
 */

public class DateValueFormatter implements IAxisValueFormatter {
    private List<String> dates;

    public DateValueFormatter(List<String> dates){
        this.dates = dates;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis){
        return dates.get((int)value);
    }
}
