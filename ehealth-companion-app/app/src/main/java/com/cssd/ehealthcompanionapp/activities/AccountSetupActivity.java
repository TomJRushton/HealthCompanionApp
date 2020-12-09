package com.cssd.ehealthcompanionapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cssd.ehealthcompanionapp.R;
import com.cssd.ehealthcompanionapp.data.services.PatientsService;
import com.cssd.ehealthcompanionapp.dtos.GenericAccount;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AccountSetupActivity extends AppCompatActivity {

    private Button dateButton;
    private TextView dobView;
    private Calendar calendar = Calendar.getInstance();
    private static SimpleDateFormat sdf;
    private NumberPicker heightNp;
    private NumberPicker weightNp;
    private RadioGroup ethnicityGroup;
    private RadioGroup genderGroup;
    private Button submitButton;
    private TextView firstNameView;
    private TextView surnameView;
    private TextView ethnicityOther;

    private String firstName;
    private String surname;
    private Date dob;
    private String gender;
    private String ethnicity;
    private int height;
    private int weight;
    private PatientsService patientsService = PatientsService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        sdf = new SimpleDateFormat("dd-MM-yyyy");
        firstNameView = findViewById(R.id.account_first_name);
        surnameView = findViewById(R.id.account_second_name);
        dobView = findViewById(R.id.dob_date);
        dateButton = findViewById(R.id.dob_button);
        ethnicityOther = findViewById(R.id.account_ethnicity_other);

        dateButton.setOnClickListener(click -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this);
            datePickerDialog.setOnDateSetListener((picker, year, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dobView.setText(sdf.format(calendar.getTime()));
            });
            datePickerDialog.setTitle("Pick Event Date");
            datePickerDialog.show();
        });

        heightNp = findViewById(R.id.height_np);
        weightNp = findViewById(R.id.weight_np);

        String[] heightValues = new String[251];
        for (int i = 0; i <= 250; i++) {
            heightValues[i] = String.valueOf(i + 50);
        }

        String[] weightValues = new String[636];
        for (int i = 0; i <= 635; i++) {
            weightValues[i] = String.valueOf(i + 20);
        }

        heightNp.setMinValue(50);
        heightNp.setMaxValue(250);
        heightNp.setDisplayedValues(heightValues);
        heightNp.setValue(170);

        weightNp.setMinValue(20);
        weightNp.setMaxValue(635);
        weightNp.setDisplayedValues(weightValues);
        weightNp.setValue(62);
        genderGroup = findViewById(R.id.radio_gender);
        ethnicityGroup = findViewById(R.id.radio_ethnicity);
        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            gender = rb.getText().toString();
        });
        ethnicityGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            ethnicity = rb.getText().toString();
        });

        submitButton = findViewById(R.id.submit_account_button);
        submitButton.setOnClickListener(click -> {
            if (firstNameView.getText() == "" || surnameView.getText() == "" || dobView.getText() == "" || gender == null || ethnicity == null || (ethnicity.equals("Mixed/Other") && ethnicityOther.getText() == "")) {
                Toast.makeText(this, "Fill in the form fully", Toast.LENGTH_SHORT).show();
            }
            else {
                readDateValues();
                patientsService.getOwnAccountId(result -> {
                    if (result.isSuccess()) {
                        updateAccount(result.get());
                    }
                });

            }
        });
    }

    private void updateAccount(String uid) {
        patientsService.update(uid, genericAccount -> {
            if (genericAccount == null){
                genericAccount = new GenericAccount();
            }
            genericAccount.setFirstName(firstName);
            genericAccount.setSurname(surname);
            genericAccount.setDob(dob);
            genericAccount.setGender(gender);
            genericAccount.setEthnicity(ethnicity);
            genericAccount.setHeight(height);
            genericAccount.setWeight(weight);
            return genericAccount;
        }, result -> {
            if (result.isSuccess()) {
                Intent intent = new Intent(this, ConnectGoogleFitActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Snackbar.make(findViewById(android.R.id.content),
                        "Error updating account", Snackbar.LENGTH_LONG);
            }
        });
    }

    private void readDateValues() {
        if (ethnicity.equals("Mixed/Other")){
            ethnicity = ethnicityOther.getText().toString();
        }
        firstName = firstNameView.getText().toString();
        surname = surnameView.getText().toString();
        dob = calendar.getTime();
        height = heightNp.getValue();
        weight = weightNp.getValue();

    }
}
