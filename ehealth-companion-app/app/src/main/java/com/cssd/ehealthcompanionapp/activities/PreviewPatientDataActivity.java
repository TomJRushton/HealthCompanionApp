package com.cssd.ehealthcompanionapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cssd.ehealthcompanionapp.R;
import com.cssd.ehealthcompanionapp.data.services.MeasurementService;
import com.cssd.ehealthcompanionapp.dtos.BloodPressure;
import com.cssd.ehealthcompanionapp.dtos.HeartRate;
import com.cssd.ehealthcompanionapp.dtos.StepCount;
import com.cssd.ehealthcompanionapp.helper.IndexAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PreviewPatientDataActivity extends AppCompatActivity {

    private MeasurementService measurementService = MeasurementService.getInstance();
    private List<BloodPressure> bloodPressures = new ArrayList<>();
    private List<StepCount> steps = new ArrayList<>();
    private List<HeartRate> heartRates = new ArrayList<>();
    private LineChart chart_step;
    private LineChart chart_heart_rate;
    private LineChart chart_blood_pressure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_patient_data);
        MeasurementService.getInstance();
        chart_step = findViewById(R.id.chart_step);
        chart_step.setTouchEnabled(true);
        chart_step.setPinchZoom(true);
        measurementService.getAllSteps(r -> {
            if (r.isSuccess()) {
                steps = r.get();
                extractStepsWithinAWeek(steps);
                System.out.println(steps);
            }
            else {
                Toast.makeText(this, "Unable to get data", Toast.LENGTH_SHORT).show();
            }
        });

        chart_heart_rate = findViewById(R.id.chart_heart_rate);
        chart_heart_rate.setTouchEnabled(true);
        chart_heart_rate.setPinchZoom(true);
        measurementService.getAllHeartRateReadings(r -> {
            if (r.isSuccess()) {
                heartRates = r.get();
                extractHeartRateWithinAWeek(heartRates);
            }
            else {
                Toast.makeText(this, "Unable to get data", Toast.LENGTH_SHORT).show();
            }
        });

        chart_blood_pressure = findViewById(R.id.chart_blood_pressure);
        chart_blood_pressure.setTouchEnabled(true);
        chart_blood_pressure.setPinchZoom(true);
        measurementService.getAllBloodPressureReadings(r -> {
            if (r.isSuccess()) {
                bloodPressures = r.get();
                extractBloodPressureWithinAWeek(bloodPressures);
            }
            else {
                Toast.makeText(this, "Unable to get data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void extractStepsWithinAWeek(List<StepCount> steps) {
        List<StepCount> stepsInAWeek = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        steps.forEach( step -> {
            c.set(Calendar.HOUR_OF_DAY, 0);
            if((c.getTimeInMillis() - step.getTimestamp().getTime())/ (24 * 60 * 60 * 1000) <= 7) {
                stepsInAWeek.add(step);
            }
        });
        if (!stepsInAWeek.isEmpty()) {
            drawStepGraph(stepsInAWeek);
        }
    }

    private void extractBloodPressureWithinAWeek(List<BloodPressure> bloodPressures) {
        List<BloodPressure> bloodPressuresInAWeek = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        bloodPressures.forEach( bloodPressure -> {
            c.set(Calendar.HOUR_OF_DAY, 0);
            if((c.getTimeInMillis() - bloodPressure.getTimestamp().getTime())/ (24 * 60 * 60 * 1000) <= 7) {
                bloodPressuresInAWeek.add(bloodPressure);
            }
        });
        if (!bloodPressuresInAWeek.isEmpty()) {
            drawBloodPressureGraph(bloodPressuresInAWeek);
        }
    }

    private void extractHeartRateWithinAWeek(List<HeartRate> steps) {
        List<HeartRate> heartRatesInAWeek = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        heartRates.forEach( heartRate -> {
            c.set(Calendar.HOUR_OF_DAY, 0);
            if((c.getTimeInMillis() - heartRate.getTimestamp().getTime())/ (24 * 60 * 60 * 1000) <= 7) {
                heartRatesInAWeek.add(heartRate);
            }
        });
        if (!heartRatesInAWeek.isEmpty()) {
            drawHeartRateGraph(heartRatesInAWeek);
        }
    }

    private void drawStepGraph(List<StepCount> steps) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        ArrayList<Entry> values = new ArrayList<>();
        for (int i=0;i<steps.size();i++) {
            values.add(new Entry(i, steps.get(i).getStepCount()));
        }
        List<String> dateValues = new ArrayList<>();

        for (StepCount step : steps) {
            dateValues.add(sdf.format(step.getTimestamp()));
        }

        LineDataSet lineDataSet = new LineDataSet(values, "Steps per day");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setValueTextSize(12);
        LineData lineData = new LineData(lineDataSet);
        chart_step.getDescription().setText("Steps done per day");
        chart_step.getDescription().setTextSize(12);
        chart_step.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        chart_step.animateY(1000);
        chart_step.getXAxis().setGranularityEnabled(true);
        chart_step.getXAxis().setGranularity(1.0f);
        XAxis xAxis = chart_step.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateValues) {
            @Override
            public String getFormattedValue(float value) {
                return dateValues.get((int) value );
            }
        });
        chart_step.setData(lineData);
    }

    private void drawBloodPressureGraph(List<BloodPressure> steps) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        ArrayList<Entry> valuesDiastolic = new ArrayList<>();
        ArrayList<Entry> valuesSistolic = new ArrayList<>();
        for (int i=0;i<bloodPressures.size();i++) {
            valuesDiastolic.add(new Entry(i, bloodPressures.get(i).getDiastolicValue()));
        }
        for (int i=0;i<bloodPressures.size();i++) {
            valuesSistolic.add(new Entry(i, bloodPressures.get(i).getSystolicValue()));
        }
        List<String> dateValues = new ArrayList<>();

        for (BloodPressure bloodPressure : steps) {
            dateValues.add(sdf.format(bloodPressure.getTimestamp()));
        }

        LineDataSet lineDataSet1 = new LineDataSet(valuesDiastolic, "Blood pressure per day");
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet1.setHighlightEnabled(true);
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setCircleRadius(6);
        lineDataSet1.setCircleHoleRadius(3);
        lineDataSet1.setDrawHighlightIndicators(true);
        lineDataSet1.setHighLightColor(Color.RED);
        lineDataSet1.setValueTextSize(12);
        LineData lineData1 = new LineData(lineDataSet1);

        LineDataSet lineDataSet2 = new LineDataSet(valuesSistolic, "Blood pressure per day");
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet2.setHighlightEnabled(true);
        lineDataSet2.setLineWidth(2);
        lineDataSet2.setCircleRadius(6);
        lineDataSet2.setCircleHoleRadius(3);
        lineDataSet2.setDrawHighlightIndicators(true);
        lineDataSet2.setHighLightColor(Color.RED);
        lineDataSet2.setValueTextSize(12);
        LineData lineData2 = new LineData(lineDataSet2);

        chart_blood_pressure.getDescription().setText("Systolic pressure per day");
        chart_blood_pressure.getDescription().setTextSize(12);
        chart_blood_pressure.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        chart_blood_pressure.animateY(1000);
        chart_blood_pressure.getXAxis().setGranularityEnabled(true);
        chart_blood_pressure.getXAxis().setGranularity(1.0f);
        XAxis xAxis = chart_blood_pressure.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateValues) {
            @Override
            public String getFormattedValue(float value) {
                return dateValues.get((int) value );
            }
        });
        chart_blood_pressure.setData(lineData1);
        chart_blood_pressure.setData(lineData2);
    }

    private void drawHeartRateGraph(List<HeartRate> heartRates) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        ArrayList<Entry> values = new ArrayList<>();
        for (int i=0;i<heartRates.size();i++) {
            values.add(new Entry(i, (float) heartRates.get(i).getAverage()));
        }
        List<String> dateValues = new ArrayList<>();

        for (HeartRate heartRate : heartRates) {
            dateValues.add(sdf.format(heartRate.getTimestamp()));
        }

        LineDataSet lineDataSet = new LineDataSet(values, "Average heart rate per day");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setValueTextSize(12);
        LineData lineData = new LineData(lineDataSet);
        chart_heart_rate.getDescription().setText("Average heart rate per day");
        chart_heart_rate.getDescription().setTextSize(12);
        chart_heart_rate.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        chart_heart_rate.animateY(1000);
        chart_heart_rate.getXAxis().setGranularityEnabled(true);
        chart_heart_rate.getXAxis().setGranularity(1.0f);
        XAxis xAxis = chart_heart_rate.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateValues) {
            @Override
            public String getFormattedValue(float value) {
                return dateValues.get((int) value );
            }
        });
        chart_heart_rate.setData(lineData);
    }
}
