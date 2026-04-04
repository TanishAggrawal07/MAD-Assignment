package com.tanish.sensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private Sensor proximitySensor;

    private TextView accelX, accelY, accelZ;
    private TextView lightValue;
    private TextView proximityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        accelX = findViewById(R.id.accel_x);
        accelY = findViewById(R.id.accel_y);
        accelZ = findViewById(R.id.accel_z);
        lightValue = findViewById(R.id.light_value);
        proximityValue = findViewById(R.id.proximity_value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX.setText(String.format("%.2f", event.values[0]));
            accelY.setText(String.format("%.2f", event.values[1]));
            accelZ.setText(String.format("%.2f", event.values[2]));
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightValue.setText(String.format("%.2f lux", event.values[0]));
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityValue.setText(String.format("%.2f cm", event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
