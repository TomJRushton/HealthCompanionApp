package com.cssd.ehealthcompanionapp.helper;

import android.util.Log;

import com.cssd.ehealthcompanionapp.dtos.GenericAccount;
import com.cssd.ehealthcompanionapp.dtos.HeartRate;
import com.cssd.ehealthcompanionapp.dtos.Measurement;
import com.cssd.ehealthcompanionapp.dtos.StepCount;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class GoogleFitHelper {

    public static void readDataSet(DataSet dataSet, GenericAccount genericAccount, Consumer<Measurement> onMeasurement) {
        for (DataPoint dp : dataSet.getDataPoints()) {
            long timestamp = dp.getTimestamp(TimeUnit.MILLISECONDS);
            timestamp = Util.getStartOfDay(timestamp);

            String patientId = genericAccount.getUid();
            switch (dp.getDataType().getName()) {
                case "com.google.heart_rate.summary":
                    double heartRateAverage = dp.getValue(Field.FIELD_AVERAGE).asFloat();
                    double heartRateMax = dp.getValue(Field.FIELD_MAX).asFloat();
                    double heartRateMin = dp.getValue(Field.FIELD_MIN).asFloat();
                    HeartRate heartRate = new HeartRate(patientId, new Date(timestamp), heartRateMin, heartRateMax, heartRateAverage);
                    onMeasurement.accept(heartRate);
                    break;
                case "com.google.step_count.delta":
                    int stepCountValue = dp.getValue(Field.FIELD_STEPS).asInt();
                    StepCount stepCount = new StepCount(patientId, new Date(timestamp), stepCountValue);
                    onMeasurement.accept(stepCount);
                    break;
            }
        }
    }

    private static void logDataSet(DataSet dataSet, GenericAccount genericAccount) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) { //Each dp for that day
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }
}
