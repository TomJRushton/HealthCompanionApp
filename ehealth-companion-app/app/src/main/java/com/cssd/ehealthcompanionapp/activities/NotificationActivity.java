package com.cssd.ehealthcompanionapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cssd.ehealthcompanionapp.R;
import com.cssd.ehealthcompanionapp.data.services.AppointmentService;
import com.cssd.ehealthcompanionapp.data.services.PatientsService;
import com.cssd.ehealthcompanionapp.database.AppointmentsRepo;
import com.cssd.ehealthcompanionapp.dtos.Appointment;

import java.util.Date;

public class NotificationActivity extends AppCompatActivity {
    private Button submitButton;
    private String doctorsMessage;
    private TextView message;
    private EditText reply;
    private AppointmentService appointmentService = AppointmentService.getInstance();
    private Button sendResponse;


    //creates the page, grabbing the layout and features for use
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_main);

        message = findViewById(R.id.notification_text_view);
        sendResponse = findViewById(R.id.send_response);
        reply = findViewById(R.id.editText3);

        sendResponse.setOnClickListener(click -> {
            getPatientId();
        });
    }

    //Once features are built, grabs data from server
    @Override
    protected void onResume() {
        super.onResume();
        getLatestAppointment();
    }


    //gets the repose from client and puts it into an appointment object to be used
    private void getResponse(String patientId){
        String replyText = reply.getText().toString();
        Appointment appointment = new Appointment();
        appointment.setMessage(replyText);
        appointment.setFrom("patient");
        appointment.setPatientId(patientId);
        appointment.setTime(new Date().getTime());


        //adds the appointment to the firebase
        appointmentService.add(appointment, result -> {
            if (result.isSuccess()){
                finish();
            }else{
                showError();
            }
        });
    }


    //gets the most recent message sent by the doctor
    private void getLatestAppointment() {
        AppointmentService.getInstance().getMostRecentAppointmentMessage(result -> {
            if (result.isSuccess()) {
                Appointment appointment = result.get();
                showLatestAppointment(appointment);
            }
        });
    }


    //grabs the patient id to be used
    private void getPatientId() {
        PatientsService.getInstance().getOwnAccountId(result -> {
            if (result.isSuccess()) {
                getResponse(result.get());
            } else {
                showError();
            }
        });
    }

    private void showLatestAppointment(Appointment appointment){
        message.setText(appointment.getMessage());
    }


    //throws an error if anything requires it
    private void showError() {
        new AlertDialog.Builder(this)
                .setTitle("Error occurred")
                .setMessage("Error occurred during sending submitted data")
                .setPositiveButton(R.string.okay_text, (dialog, which) -> {
                    finish();
                }).show();
    }
}
