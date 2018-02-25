package com.sjtu.se.temperaturemonitor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.sjtu.se.temperaturemonitor.formater.DateValueFormatter;
import com.sjtu.se.temperaturemonitor.formater.HourValueFormatter;
import com.sjtu.se.temperaturemonitor.model.Content;
import com.sjtu.se.temperaturemonitor.model.DailyData;
import com.sjtu.se.temperaturemonitor.view.CustomMarkerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity{

    private LineChart historyChart;
    private LineChart dailyChart;
    private TextView tAvgTemp;
    private TextView tMaxTemp;
    private TextView tMinTemp;
    private TextView tAvgHumid;
    private TextView tDate;
    private boolean firstInit;
    private List<DailyData> dailyDatas;
    private List<String> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewBinding();
        chartInitialize();
    }

    private void viewBinding() {
        tAvgTemp = (TextView) findViewById(R.id.avg_temp);
        tMaxTemp = (TextView) findViewById(R.id.max_temp);
        tMinTemp = (TextView) findViewById(R.id.min_temp);
        tAvgHumid = (TextView) findViewById(R.id.avg_humid);
        tDate = (TextView) findViewById(R.id.date);
        dailyChart = (LineChart) findViewById(R.id.daily_chart);
        historyChart = (LineChart) findViewById(R.id.history_chart);
    }

    private void initChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragDecelerationEnabled(true);
        chart.setDragDecelerationFrictionCoef(0.9f);
        chart.setDragEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(getResources().getColor(R.color.chartBackground));

        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);
    }

    private void generateDailyChart() {
        dailyChart.setOnChartValueSelectedListener(new DailyValueSelectedListener());
        initChart(dailyChart);
    }

    private void generateHistoryChart() {
        historyChart.setOnChartValueSelectedListener(new HistoryValueSelectedListener());
        initChart(historyChart);

        setHistoryChartData();

        historyChart.highlightValue(historyChart.getXChartMax(), 1);

        setAxis(historyChart, new DateValueFormatter(dates));
    }

    private void chartInitialize() {
        firstInit = true;
        generateDailyChart();
        generateHistoryChart();
    }

    private void setAxis(LineChart chart, IAxisValueFormatter formatter){
        chart.setVisibleXRangeMaximum(3);

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(getResources().getColor(R.color.labelText));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(getResources().getColor(R.color.labelText));
        xAxis.setDrawAxisLine(false);
        xAxis.setAxisLineColor(getResources().getColor(R.color.chartGridLine));
        xAxis.setValueFormatter(formatter);
        xAxis.setLabelCount(3);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.labelText));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(getResources().getColor(R.color.chartGridLine));
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.chartGridLine));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(getResources().getColor(R.color.labelText));
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
        rightAxis.setAxisLineColor(getResources().getColor(R.color.chartGridLine));
    }

    private void renewDailyChart(int index) {
        setDailyChartData(index);
        setAxis(dailyChart, new HourValueFormatter());
        dailyChart.centerViewTo(0, 0, dailyChart.getData().getDataSetByIndex(0)
                .getAxisDependency());
        dailyChart.highlightValue(null);
    }

    private void setDailyChartData(int index) {
        List<Content> contents = dailyDatas.get(index).getContents();
        ArrayList<Entry> temp = new ArrayList<Entry>();
        ArrayList<Entry> humid = new ArrayList<Entry>();

        int n = contents.size();
        for(int i = 0; i < n; i++){
            temp.add(new Entry(i, contents.get(i).getTemperature(),1));
            humid.add(new Entry(i, contents.get(i).getHumidity(),0));
        }

        LineDataSet tempSet, humidSet;

        if(dailyChart.getData() != null &&
                dailyChart.getData().getDataSetCount() >=2 ) {
            tempSet = (LineDataSet)dailyChart.getData().getDataSetByIndex(1);
            humidSet = (LineDataSet)dailyChart.getData().getDataSetByIndex(0);
            tempSet.setValues(temp);
            humidSet.setValues(humid);
            dailyChart.getData().notifyDataChanged();
            dailyChart.notifyDataSetChanged();
        }
        else{
            tempSet = new LineDataSet(temp, "气温");
            tempSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            tempSet.setColor(getResources().getColor(R.color.maxTemperature));
            tempSet.setCircleColor(getResources().getColor(R.color.maxTemperature));
            tempSet.setLineWidth(3f);
            tempSet.setCircleRadius(5f);
            setHighLight(tempSet, 2f);
            tempSet.setDrawCircleHole(true);

            humidSet = new LineDataSet(humid, "湿度");
            humidSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            humidSet.setColor(getResources().getColor(R.color.avgHumidity));
            humidSet.setCircleColor(getResources().getColor(R.color.avgHumidity));
            humidSet.setLineWidth(3f);
            humidSet.setCircleRadius(5f);
            setHighLight(humidSet, 2f);
            humidSet.setDrawCircleHole(true);
            humidSet.setDrawFilled(true);
            humidSet.setFillAlpha(30);
            humidSet.setFillColor(getResources().getColor(R.color.avgHumidity));
            humidSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData data = new LineData(humidSet, tempSet);
            data.setDrawValues(false);
            data.setValueTextColor(getResources().getColor(R.color.labelText));
            data.setValueTextSize(9f);

            dailyChart.setData(data);
        }
    }

    private void setHistoryChartData() {
        getDailyDatas();
        ArrayList<Entry> maxTemp = new ArrayList<Entry>();
        ArrayList<Entry> minTemp = new ArrayList<Entry>();
        ArrayList<Entry> avgHumid = new ArrayList<Entry>();
        dates = new ArrayList<String>();

        int n = dailyDatas.size();
        for(int i = 0; i < n; i++){
            maxTemp.add(new Entry(i, dailyDatas.get(i).getMaxTemp(),1));
            minTemp.add(new Entry(i, dailyDatas.get(i).getMinTemp(),2));
            avgHumid.add(new Entry(i, dailyDatas.get(i).getAvgHumid(),0));
            dates.add(dailyDatas.get(i).getDate());
        }

        LineDataSet maxTempSet, minTempSet, avgHumidSet;

        if(historyChart.getData() != null &&
                historyChart.getData().getDataSetCount() >= 3) {
            maxTempSet = (LineDataSet)historyChart.getData().getDataSetByIndex(1);
            minTempSet = (LineDataSet)historyChart.getData().getDataSetByIndex(2);
            avgHumidSet = (LineDataSet)historyChart.getData().getDataSetByIndex(0);
            maxTempSet.setValues(maxTemp);
            minTempSet.setValues(minTemp);
            avgHumidSet.setValues(avgHumid);
            historyChart.getData().notifyDataChanged();
            historyChart.notifyDataSetChanged();
        }
        else {
            maxTempSet = new LineDataSet(maxTemp, "最高气温");
            maxTempSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            maxTempSet.setColor(getResources().getColor(R.color.maxTemperature));
            maxTempSet.setCircleColor(getResources().getColor(R.color.maxTemperature));
            maxTempSet.setLineWidth(2f);
            maxTempSet.setCircleRadius(3f);
            setHighLight(maxTempSet, 12f);
            maxTempSet.setDrawCircleHole(true);

            minTempSet = new LineDataSet(minTemp, "最低气温");
            minTempSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            minTempSet.setColor(getResources().getColor(R.color.minTemperature));
            minTempSet.setCircleColor(getResources().getColor(R.color.minTemperature));
            minTempSet.setLineWidth(2f);
            minTempSet.setCircleRadius(3f);
            setHighLight(minTempSet, 12f);
            minTempSet.setDrawCircleHole(true);

            avgHumidSet = new LineDataSet(avgHumid, "平均湿度");
            avgHumidSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            avgHumidSet.setColor(getResources().getColor(R.color.avgHumidity));
            avgHumidSet.setCircleColor(getResources().getColor(R.color.avgHumidity));
            avgHumidSet.setLineWidth(2f);
            avgHumidSet.setCircleRadius(3f);
            setHighLight(avgHumidSet, 12f);
            avgHumidSet.setDrawCircleHole(true);
            avgHumidSet.setDrawFilled(true);
            avgHumidSet.setFillAlpha(30);
            avgHumidSet.setFillColor(getResources().getColor(R.color.avgHumidity));
            avgHumidSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData data = new LineData(avgHumidSet, maxTempSet, minTempSet);
            data.setDrawValues(false);
            data.setValueTextColor(getResources().getColor(R.color.labelText));
            data.setValueTextSize(9f);

            historyChart.setData(data);
        }
    }

    private void getDailyDatas(){
        dailyDatas = randGenDailyDatas();
    }

    private List<DailyData> randGenDailyDatas(){
        List<DailyData> ldd = new ArrayList<DailyData>();
        for(int i = 0; i < 10; i ++){
            DailyData dd = new DailyData();
            dd.setDate("2017-10-"+i);
            List<Content> cts = new ArrayList<Content>();
            for(int j = 0; j < 24; j++){
                Content ct = new Content();
                ct.setHumidity((float)(Math.random() * 100) % 100);
                ct.setTemperature((float)(Math.random() * 70) % 70 - 35);
                cts.add(ct);
            }
            dd.setContents(cts);
            ldd.add(dd);
        }
        return ldd;
    }

    private void setHighLight(LineDataSet set, float width) {
        set.setHighLightColor(getResources().getColor(R.color.chartGridLine));
        set.setDrawHorizontalHighlightIndicator(false);
        set.setHighlightLineWidth(width);
    }

    public class HistoryValueSelectedListener implements OnChartValueSelectedListener{
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            Log.i("Entry selected", e.toString());

            if(!firstInit){
                historyChart.centerViewToAnimated(e.getX(), e.getY(), historyChart.getData().getDataSetByIndex(h.getDataSetIndex())
                        .getAxisDependency(), 500);
            }
            else {
                historyChart.centerViewTo(e.getX(), e.getY(), historyChart.getData().getDataSetByIndex(h.getDataSetIndex())
                        .getAxisDependency());
                firstInit = false;
            }
            int index = historyChart.getData().getDataSetByIndex(h.getDataSetIndex()).getEntryIndex(e);
            DecimalFormat df = new DecimalFormat("0.0");
            tAvgTemp.setText(df.format(dailyDatas.get(index).getAvgTemp()));
            tMaxTemp.setText(df.format(dailyDatas.get(index).getMaxTemp()));
            tMinTemp.setText(df.format(dailyDatas.get(index).getMinTemp()));
            tAvgHumid.setText(df.format(dailyDatas.get(index).getAvgHumid()));
            tDate.setText(dailyDatas.get(index).getDate());
            renewDailyChart(index);
        }

        @Override
        public void onNothingSelected() {
            Log.i("Nothing selected", "Nothing selected.");
        }
    }

    public class DailyValueSelectedListener implements OnChartValueSelectedListener{
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            Log.i("Entry selected", e.toString());

            dailyChart.centerViewToAnimated(e.getX(), e.getY(), dailyChart.getData().getDataSetByIndex(h.getDataSetIndex())
                    .getAxisDependency(), 500);
        }

        @Override
        public void onNothingSelected() {
            Log.i("Nothing selected", "Nothing selected.");
        }
    }
}
