package com.cssd.ehealthcompanionapp.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cssd.ehealthcompanionapp.BuildConfig;
import com.cssd.ehealthcompanionapp.receivers.AlarmReceiver;

import java.util.Calendar;

public class MeasurementSchedulerService {

    public static boolean isAlarmSet(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent("com.cssd.ehealthcompanionapp.ALARM_ID"), PendingIntent.FLAG_NO_CREATE) != null;
    }

    public static void setAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("com.cssd.ehealthcompanionapp.ALARM_ID");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, pendingIntent);
    }

}
