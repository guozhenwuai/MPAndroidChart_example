package com.sjtu.se.temperaturemonitor.model;

public class Content {
    private float temperature;
    private float humidity;

    public void setTemperature(float temp) {
        this.temperature = temp;
    }
    public float getTemperature() {
        return this.temperature;
    }
    public void setHumidity(float hum) {
        this.humidity = hum;
    }
    public float getHumidity() {
        return this.humidity;
    }

}
