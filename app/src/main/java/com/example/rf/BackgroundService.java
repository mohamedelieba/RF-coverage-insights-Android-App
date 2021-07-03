package com.example.rf;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.json.JSONException;

public class BackgroundService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (AppActivity.firstSimJson.get("imsi") != null) {
                new CallAPI().execute("https://immense-journey-36861.herokuapp.com/measurment/DML/post", AppActivity.firstSimJson.toString());
            }
            if(AppActivity.secondSimJson.get("imsi")!=null) {
                new CallAPI().execute("https://immense-journey-36861.herokuapp.com/measurment/DML/post", AppActivity.secondSimJson.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh(2000 * 60);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void refresh(int milliseconds) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), BackgroundService.class));
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }
}
