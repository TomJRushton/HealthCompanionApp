package com.cssd.ehealthcompanionapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cssd.ehealthcompanionapp.services.MeasurementSchedulerService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if(!MeasurementSchedulerService.isAlarmSet(context)){
                MeasurementSchedulerService.setAlarm(context);
            }
        }
    }
}
