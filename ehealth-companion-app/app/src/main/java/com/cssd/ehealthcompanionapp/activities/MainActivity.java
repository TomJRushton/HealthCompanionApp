package com.cssd.ehealthcompanionapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.cssd.ehealthcompanionapp.R;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private CardView bloodPreasureCard;
    private CardView patientDataCard;
    private CardView notificationCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bloodPreasureCard = findViewById(R.id.blood_pressure_card);
        patientDataCard = findViewById(R.id.patient_data);
        notificationCard = findViewById(R.id.notification_data);

        bloodPreasureCard.setOnClickListener(card -> {
            Intent nextIntent = new Intent(this, BloodPressureReadingActivity.class);
            sharedPreferences = getSharedPreferences(
                    "ehealth_settings", Context.MODE_PRIVATE);
            if (sharedPreferences.getString("BLOOD_PRESSURE_TUTORIAL", "false").equals("false")) {
                Intent intent = new Intent(this, TutorialActivity.class);
                intent.putExtra("intent", nextIntent);
                intent.setAction("blood_pressure");
                startActivity(intent);
            }
            else {
                startActivity(nextIntent);
            }
        });

        patientDataCard.setOnClickListener(card -> {
            Intent nextIntent = new Intent(this, PreviewPatientDataActivity.class);
            startActivity(nextIntent);
        });

        notificationCard.setOnClickListener(card -> {
            Intent nextIntent = new Intent(this, NotificationActivity.class);
            startActivity(nextIntent);
        });
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu:
                Intent intent = new Intent(this, AccountSetupActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
