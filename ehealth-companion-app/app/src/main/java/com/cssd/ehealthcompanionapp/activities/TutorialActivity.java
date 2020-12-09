package com.cssd.ehealthcompanionapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cssd.ehealthcompanionapp.R;

import java.util.Objects;

public class TutorialActivity extends AppCompatActivity {

    private TextView tutorialText;
    private TextView tutorialTitle;
    private ImageView tutorialImage;
    private Button okayButton;
    private Button dontShowAgainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        switch(Objects.requireNonNull(getIntent().getAction())){
            case "blood_pressure":
                tutorialImage = findViewById(R.id.tutorial_image);
                tutorialTitle = findViewById(R.id.tutorial_title);
                tutorialText = findViewById(R.id.tutorial_text);

                tutorialImage.setImageResource(R.drawable.blood_pressure);
                tutorialTitle.setText(R.string.blood_pressure_reading_tutorial_title);
                tutorialText.setText(R.string.blood_pressure_reading_tutorial);
        }
        okayButton = findViewById(R.id.tutorial_okay_button);
        okayButton.setOnClickListener(click -> {
            startActivity(Objects.requireNonNull(getIntent().getExtras()).getParcelable("intent"));
            finish();
        });
        dontShowAgainButton = findViewById(R.id.tutorial_dont_show_again);
        dontShowAgainButton.setOnClickListener(click -> {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    "ehealth_settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("BLOOD_PRESSURE_TUTORIAL", "true");
            editor.apply();
            startActivity(Objects.requireNonNull(getIntent().getExtras()).getParcelable("intent"));
            finish();
        });




    }
}
