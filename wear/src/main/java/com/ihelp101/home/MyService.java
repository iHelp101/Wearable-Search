package com.ihelp101.home;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.v4.app.NotificationCompat;


public class MyService extends Service implements SensorEventListener{

    private static final int SPEECH_REQUEST_CODE = 0;
    public SensorManager mSensorManager;
    public Sensor mGyroSensor;
    public static Context context;
    private PowerManager.WakeLock wl;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        System.out.println("Service Created!");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        PackageManager PM= this.getPackageManager();
        boolean gyro = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);


        if (gyro) {
            System.out.println("One step closer");
            mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onStart(Intent intent, int startid) {

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        float angularXSpeed = event.values[0];

        if (angularXSpeed >= 15) {
            System.out.println("Google Display!");

            Vibrator v = (Vibrator) MyActivity.c.getSystemService(MyActivity.c.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);

            Intent i = new Intent(MyActivity.c, GoogleSearchPopUp.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyActivity.c.startActivity(i);
        }
    }

    protected void onResume() {
        // Register a listener for the sensor.
        mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        // important to unregister the sensor when the activity pauses.
        mSensorManager.unregisterListener(this);
    }
}
