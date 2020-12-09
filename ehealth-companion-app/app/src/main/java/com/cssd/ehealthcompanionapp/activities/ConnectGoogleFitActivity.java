package com.cssd.ehealthcompanionapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cssd.ehealthcompanionapp.R;
import com.cssd.ehealthcompanionapp.data.services.MeasurementService;
import com.cssd.ehealthcompanionapp.services.MeasurementSchedulerService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.prefs.Preferences;

import static com.cssd.ehealthcompanionapp.parameters.RequestCodes.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;

public class ConnectGoogleFitActivity extends AppCompatActivity {

    private GoogleSignInAccount account;
    private FitnessOptions fitnessOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_google_fit);
        Button connectFit = findViewById(R.id.connect_button);
        Button skipButton = findViewById(R.id.skip_button);
        skipButton.setOnClickListener(this::startMenu);
        connectFit.setOnClickListener(this::connectGoogleFit);
        checkFit(null);
    }

    public void checkFit(View view) {
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .build();

        account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            startMenu(null);
        }
    }

    private void connectGoogleFit(@Nullable View view){
        GoogleSignIn.requestPermissions(
                this, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                account,
                fitnessOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                startMenu(null);
            }
        }
    }

    public void startMenu(@Nullable View view){
        if(!MeasurementSchedulerService.isAlarmSet(this)){
            MeasurementSchedulerService.setAlarm(this);
        }

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean firstRun = sharedPref.getBoolean("firstRun", true);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putBoolean("firstRun", false);
        edit.apply();

        if(firstRun) {
            MeasurementService.getInstance().allPatientGoogleFit(this, result -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
