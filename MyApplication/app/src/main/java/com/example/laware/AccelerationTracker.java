package com.example.laware;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * EXAMPLE: Motion sensor (> accelerometer)
 * https://developer.android.com/guide/topics/sensors/sensors_motion.html
 * https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
 */
public class AccelerationTracker implements SensorTracker, SensorEventListener{

    private final static String TAG = AccelerationTracker.class.getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isActive;

    /**
     * Constructor
     * @param context
     */
    public AccelerationTracker(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Accelerometer
        isActive=false;
    }

    @Override
    public void start() {
        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        isActive=true;
        Log.d(TAG,"Accelerometer::start");
    }

    @Override
    public void stop() {
        if(!isActive) return;
        mSensorManager.unregisterListener(this);
        isActive=false;
        Log.d(TAG,"Accelerometer::stop");
    }

    public void onSensorChanged(SensorEvent event){
        Log.d(TAG,"Acceleration("+event.values[0]+","+event.values[1]+","+event.values[2]+")");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
