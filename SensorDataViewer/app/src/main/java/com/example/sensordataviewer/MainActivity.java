package com.example.sensordataviewer;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, lightSensor, proximitySensor;
    private TextView accelX, accelY, accelZ, lightValue, proximityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure your XML is named activity_main.xml

        // UI Initialization
        accelX = findViewById(R.id.accelX);
        accelY = findViewById(R.id.accelY);
        accelZ = findViewById(R.id.accelZ);
        lightValue = findViewById(R.id.lightValue);
        proximityValue = findViewById(R.id.proximityValue);

        // Sensor Initialization
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            Toast.makeText(this, "No Proximity Sensor Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelX.setText(String.format("X Axis: %.2f", values[0]));
                accelY.setText(String.format("Y Axis: %.2f", values[1]));
                accelZ.setText(String.format("Z Axis: %.2f", values[2]));

                if (Math.abs(values[0]) > 12) accelX.setTextColor(Color.RED);
                else accelX.setTextColor(Color.parseColor("#03DAC5"));
                break;

            case Sensor.TYPE_LIGHT:
                lightValue.setText(String.format("Brightness: %.2f lx", values[0]));
                if (values[0] < 10) lightValue.setTextColor(Color.GRAY);
                else lightValue.setTextColor(Color.parseColor("#FFEB3B"));
                break;

            case Sensor.TYPE_PROXIMITY:
                float dist = values[0];
                if (proximitySensor != null && dist < proximitySensor.getMaximumRange()) {
                    proximityValue.setText("Object Detected: NEAR");
                    proximityValue.setTextColor(Color.RED);
                } else {
                    proximityValue.setText(String.format("Distance: %.2f cm", dist));
                    proximityValue.setTextColor(Color.parseColor("#BB86FC"));
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        if (lightSensor != null) sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        if (proximitySensor != null) sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}