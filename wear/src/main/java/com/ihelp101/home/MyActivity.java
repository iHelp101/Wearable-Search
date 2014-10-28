package com.ihelp101.home;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;

public class MyActivity extends Activity {

    public static Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        c = this;

        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // Now you can access your views
                final Button button = (Button) findViewById(R.id.button);

                boolean Check = isMyServiceRunning(MyService.class);

                if (Check == false) {
                    startService(new Intent(MyActivity.this, MyService.class));
                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean Check = isMyServiceRunning(MyService.class);

                        if (Check == false) {
                            startService(new Intent(MyActivity.this, MyService.class));
                            button.setText("Turn Off");
                        } else {
                            stopService(new Intent(MyActivity.this, MyService.class));
                            button.setText("Turn On");
                        }
                    }
                });
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

