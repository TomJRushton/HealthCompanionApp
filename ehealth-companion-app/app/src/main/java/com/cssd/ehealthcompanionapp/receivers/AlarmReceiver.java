package com.cssd.ehealthcompanionapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cssd.ehealthcompanionapp.services.GoogleFitService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.cssd.ehealthcompanionapp.ALARM_ID")) {
            Intent intent2 = new Intent(context, GoogleFitService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent2);
            } else {
                context.startService(intent2);
            }
        }
    }
}
