package com.ihelp101.home;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class MyService extends Service implements SensorEventListener{

    public SensorManager mSensorManager;
    public Sensor mGyroSensor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        System.out.println("Service Created!");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        PackageManager PM= this.getPackageManager();
        boolean gyro = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);

        if (gyro) {
            System.out.println("One step closer");
            mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void Yolo () {
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();

        Log.v("Home", "Screen: " +isScreenOn);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Yolo();
            }
        }, 20000);
    }

    public void onDestroy(){
        System.out.println("Service Killed");
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        float angularXSpeed = event.values[0];

        if (angularXSpeed >= 15) {
            PowerManager pm = (PowerManager)
                    getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isInteractive();

            boolean isCharge = isPlugged(MyActivity.c);

            if (isScreenOn == true && isCharge == false) {
                System.out.println("Google Display!");
                System.out.println(angularXSpeed);

                Vibrator v = (Vibrator) MyActivity.c.getSystemService(MyActivity.c.VIBRATOR_SERVICE);
                v.vibrate(250);

                Intent i = new Intent(MyActivity.c, GoogleSearchPopUp.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyActivity.c.startActivity(i);
            }
         } else {
                if (angularXSpeed > 5) {
                    System.out.println(angularXSpeed);
                }
            }
         }


    public static boolean isPlugged(Context context) {
        boolean isPlugged= false;
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        return isPlugged;
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
