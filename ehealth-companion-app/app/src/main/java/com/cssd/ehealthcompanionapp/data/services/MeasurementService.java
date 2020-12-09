package com.cssd.ehealthcompanionapp.data.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cssd.ehealthcompanionapp.data.services.support.ResultCheck;
import com.cssd.ehealthcompanionapp.data.services.support.ServiceResult;
import com.cssd.ehealthcompanionapp.dtos.BloodPressure;
import com.cssd.ehealthcompanionapp.dtos.GenericAccount;
import com.cssd.ehealthcompanionapp.dtos.HeartRate;
import com.cssd.ehealthcompanionapp.dtos.Measurement;
import com.cssd.ehealthcompanionapp.dtos.StepCount;
import com.cssd.ehealthcompanionapp.helper.GoogleFitHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.cssd.ehealthcompanionapp.helper.GoogleFitHelper.readDataSet;
import static com.cssd.ehealthcompanionapp.helper.Util.TAG;
import static com.cssd.ehealthcompanionapp.helper.Util.getStartOfDay;
import static java.util.stream.Collectors.toList;

public class MeasurementService {
    private MeasurementService() {
    }

    private static MeasurementService measurementService;

    synchronized public static MeasurementService getInstance() {
        if (measurementService == null)
            measurementService = new MeasurementService();
        return measurementService;
    }


    public void add(Measurement measurement, Consumer<ResultCheck<Measurement>> result) {
        ServiceResult<Measurement> r = new ServiceResult<>(result);
        FirebaseDatabase.getInstance()
                .getReference()
                .getRoot()
                .child(measurement.getType())
                .child(measurement.getPatientId())
                .child(Long.toString(measurement.getTimestamp().getTime()))
                .setValue(measurement)
                .addOnSuccessListener(result2 -> r.accept(measurement))
                .addOnFailureListener(r::fail);
    }


    public void syncPatientGoogleFit(Context context, Consumer<ResultCheck<Void>> result) {
        ServiceResult<Void> r = new ServiceResult<>(result);

        PatientsService.getInstance().getOwnAccount(accountResult -> {
            if(accountResult.isSuccess()) {
                syncGoogleFit(context, 1, accountResult.get(), r::resolve);
            } else {
                r.fail(accountResult);
            }
        });
    }

    public void allPatientGoogleFit(Context context, Consumer<ResultCheck<Void>> result) {
        ServiceResult<Void> r = new ServiceResult<>(result);

        PatientsService.getInstance().getOwnAccount(accountResult -> {
            if(accountResult.isSuccess()) {
                syncGoogleFit(context, 365,accountResult.get(), r::resolve);
            } else {
                r.fail(accountResult);
            }
        });
    }

    public void syncGoogleFit(Context context, int days, GenericAccount genericAccount, Consumer<ResultCheck<Void>> result) {
        ServiceResult<Void> r = new ServiceResult<>(result);

        Toast.makeText(context, "Syncing Google Fit", Toast.LENGTH_LONG).show();

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            r.fail("Google Fit Permissions Error");
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            cal.setTime(new Date());
            long endTime = getStartOfDay(cal.getTimeInMillis());
            cal.add(Calendar.DATE, -days);
            long startTime = getStartOfDay(cal.getTimeInMillis());

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .build();

            account = GoogleSignIn
                    .getAccountForExtension(context, fitnessOptions);

            Fitness.getHistoryClient(context, account)
                    .readData(readRequest)
                    .addOnSuccessListener(response -> {
                        Log.d(GoogleFitHelper.class.getSimpleName(), "OnSuccess()");
                        if (response.getBuckets().size() > 0) {
                            r.accept();
                            for (Bucket bucket : response.getBuckets()) { //Day Bucket
                                List<DataSet> dataSets = bucket.getDataSets();
                                for (DataSet dataSet : dataSets) { //Each Fitness Type
                                    readDataSet(dataSet, genericAccount, measurement -> {
                                        add(measurement, result2 -> {
                                            if(result2.isFail()) {
                                                Log.e(TAG(this), "Add Measurement Error", result2.getException());
                                            }
                                        });
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        r.fail(e);
                        Log.e(TAG(this), "Google Fit Client Request Error", e);
                    });
        }
    }

    public void getAllSteps(Consumer<ResultCheck<List<StepCount>>> result) {
        ServiceResult<List<StepCount>> r = new ServiceResult<>(result);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference ref = firebaseDatabase.getReference().child("stepCount").child(currentUser.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<StepCount> steps = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        StepCount stepCount = ds.getValue(StepCount.class);
                        steps.add(stepCount);
                    }
                    r.accept(steps);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


    public void getAllHeartRateReadings(Consumer<ResultCheck<List<HeartRate>>> result) {
        ServiceResult<List<HeartRate>> r = new ServiceResult<>(result);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference ref = firebaseDatabase.getReference().child("heartRate").child(currentUser.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<HeartRate> heartRates = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        HeartRate heartRate = ds.getValue(HeartRate.class);
                        heartRates.add(heartRate);
                    }
                    r.accept(heartRates);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public void getAllBloodPressureReadings(Consumer<ResultCheck<List<BloodPressure>>> result) {
        ServiceResult<List<BloodPressure>> r = new ServiceResult<>(result);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference ref = firebaseDatabase.getReference().child("bloodPressure").child(currentUser.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<BloodPressure> bloodPressures = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        BloodPressure bloodPressure = ds.getValue(BloodPressure.class);
                        bloodPressures.add(bloodPressure);
                    }
                    r.accept(bloodPressures);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
