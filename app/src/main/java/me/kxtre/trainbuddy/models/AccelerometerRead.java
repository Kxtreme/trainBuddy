package me.kxtre.trainbuddy.models;

import android.hardware.SensorEvent;

public class AccelerometerRead {
    private Float x;
    private Float y;
    private Float z;
    private Long timestamp;

    public AccelerometerRead(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        timestamp = event.timestamp;
    }
}
