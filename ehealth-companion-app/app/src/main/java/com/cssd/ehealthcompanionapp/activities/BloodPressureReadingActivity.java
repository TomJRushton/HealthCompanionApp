package com.cssd.ehealthcompanionapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cssd.ehealthcompanionapp.R;
import com.cssd.ehealthcompanionapp.data.services.MeasurementService;
import com.cssd.ehealthcompanionapp.data.services.PatientsService;
import com.cssd.ehealthcompanionapp.dtos.BloodPressure;

public class BloodPressureReadingActivity extends AppCompatActivity {
    private NumberPicker systolicNumberPicker;
    private NumberPicker diastolicNumberPicker;
    private Button submitButton;
    private String patientsId;
    private MeasurementService measurementService = MeasurementService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blood_pressure_reading);

        systolicNumberPicker = findViewById(R.id.systolic_number_picker);
        diastolicNumberPicker = findViewById(R.id.diabolic_number_picker);
        submitButton = findViewById(R.id.submit_button);

        String[] systolicValues = new String[161];
        for (int i = 0; i <= 160; i++) {
            systolicValues[i] = String.valueOf(i + 90);
        }

        String[] diastolicValues = new String[81];
        for (int i = 0; i <= 80; i++) {
            diastolicValues[i] = String.valueOf(i + 60);
        }

        systolicNumberPicker.setMinValue(90);
        systolicNumberPicker.setMaxValue(250);
        systolicNumberPicker.setDisplayedValues(systolicValues);

        diastolicNumberPicker.setMinValue(60);
        diastolicNumberPicker.setMaxValue(140);
        diastolicNumberPicker.setDisplayedValues(diastolicValues);

        MeasurementService.getInstance();

        submitButton.setOnClickListener(click -> {
            getPatientId();
        });
    }

    private void getPatientId() {
        PatientsService.getInstance().getOwnAccountId(result -> {
            if (result.isSuccess()) {
                sendBloodPressure(result.get());
            } else {
                showError();
            }
        });
    }

    private void sendBloodPressure(String patientsId) {
        BloodPressure bloodPressure = new BloodPressure(patientsId, systolicNumberPicker.getValue(), diastolicNumberPicker.getValue());
        measurementService.add(bloodPressure, result -> {
            if (result.isSuccess()) {
                finish();
            } else {
                showError();
            }
        });
    }

    private void showError() {
        new AlertDialog.Builder(this)
                .setTitle("Error occurred")
                .setMessage("Error occurred during sending submitted data")
                .setPositiveButton(R.string.okay_text, (dialog, which) -> {
                    finish();
                }).show();
    }
}
