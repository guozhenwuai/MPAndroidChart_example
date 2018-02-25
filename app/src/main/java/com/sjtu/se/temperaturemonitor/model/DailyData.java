package com.sjtu.se.temperaturemonitor.model;

import java.util.List;

/**
 * Created by xinywu on 2017/11/3.
 */

public class DailyData {
    private List<Content> contents;
    private String date;
    private float maxTemp;
    private float minTemp;
    private float avgTemp;
    private float avgHumid;

    public void setDate(String date){
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getMaxTemp() {
        return this.maxTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMinTemp() {
        return this.minTemp;
    }

    public void setAvgTemp(float avgTemp) {
        this.avgTemp = avgTemp;
    }

    public float getAvgTemp() {
        return this.avgTemp;
    }

    public void setAvgHumid(float avgHumid) {
        this.avgHumid = avgHumid;
    }

    public float getAvgHumid() {
        return this.avgHumid;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
        if(contents.size() == 0) {
            maxTemp = 0;
            minTemp = 0;
            avgHumid = 0;
            avgTemp = 0;
            return;
        }

        maxTemp = Float.MIN_VALUE;
        minTemp = Float.MAX_VALUE;
        float sumTemp = 0, sumHumid = 0;

        for(Content c:contents){
            sumTemp += c.getTemperature();
            sumHumid += c.getHumidity();
            if(maxTemp < c.getTemperature()){
                maxTemp = c.getTemperature();
            }
            if(minTemp > c.getTemperature()){
                minTemp = c.getTemperature();
            }
        }

        avgTemp = sumTemp/contents.size();
        avgHumid = sumHumid/contents.size();
    }

    public List<Content> getContents() {
        return this.contents;
    }
}
