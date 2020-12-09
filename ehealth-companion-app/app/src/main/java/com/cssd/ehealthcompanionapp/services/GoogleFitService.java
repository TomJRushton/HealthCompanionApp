package com.cssd.ehealthcompanionapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cssd.ehealthcompanionapp.R;
import com.cssd.ehealthcompanionapp.activities.LoginActivity;
import com.cssd.ehealthcompanionapp.data.services.MeasurementService;
import com.cssd.ehealthcompanionapp.data.services.PatientsService;
import com.cssd.ehealthcompanionapp.dtos.GenericAccount;
import com.cssd.ehealthcompanionapp.parameters.IntentKeys;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;

public class GoogleFitService extends Service {
    public GoogleFitService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        syncGoogleFit();
        return Service.START_STICKY;
    }

    public void syncGoogleFit(){
        startForeground(IntentKeys.NOTIFICATION_ID.CHANNEL_GOOGLE_FIT_SYNC_ID, createNotification(this));

        syncMeasurements();

        if(!MeasurementSchedulerService.isAlarmSet(this)){
            MeasurementSchedulerService.setAlarm(this);
        }

        this.stopSelf();
    }

    public static Notification createNotification(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ehealth);
        builder.setContentTitle("Syncing With Google Fit");
        Intent intent = new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                    "My Notification Name", NotificationManager.IMPORTANCE_NONE);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }
        return builder.build();
    }

    private void syncMeasurements() {
        Date startTime = new Date();
        MeasurementService.getInstance().syncPatientGoogleFit(this, result -> {
            if(result.isSuccess()) {
                Date endTime = new Date();
                int time = (int) (endTime.getTime() - startTime.getTime());
                Toast.makeText(this, "Google Sync Complete in " + time + " millis", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Google Sync Failed: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
