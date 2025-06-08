package com.arishay.pawconnect;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.*;

import java.util.*;

public class AdminDashboardActivity extends AppCompatActivity {

    private PieChart userPieChart;
    private BarChart requestBarChart;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        userPieChart = findViewById(R.id.userPieChart);
        requestBarChart = findViewById(R.id.requestBarChart);
        db = FirebaseFirestore.getInstance();

        loadUserStats();
        loadRequestStats();

        // Setup admin footer navigation
        AdminFooterNavigation.setupFooterNavigation(this);
    }

    private void loadUserStats() {
        db.collection("users").get().addOnSuccessListener(query -> {
            int admins = 0, rescuers = 0, adopters = 0;

            for (DocumentSnapshot doc : query) {
                String role = doc.getString("role");
                if ("admin".equalsIgnoreCase(role)) admins++;
                else if ("rescuer".equalsIgnoreCase(role)) rescuers++;
                else if ("adopter".equalsIgnoreCase(role)) adopters++;
            }

            List<PieEntry> entries = new ArrayList<>();
            if (admins > 0) entries.add(new PieEntry(admins, "Admin"));
            if (rescuers > 0) entries.add(new PieEntry(rescuers, "Rescuer"));
            if (adopters > 0) entries.add(new PieEntry(adopters, "Adopter"));

            PieDataSet dataSet = new PieDataSet(entries, "Users by Role");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData data = new PieData(dataSet);
            data.setValueTextSize(14f);
            data.setValueTextColor(Color.WHITE);

            userPieChart.setData(data);
            userPieChart.setUsePercentValues(true);
            userPieChart.getDescription().setEnabled(false);
            userPieChart.setDrawHoleEnabled(false);
            userPieChart.animateY(1400);
            userPieChart.invalidate();
        });
    }

    private void loadRequestStats() {
        db.collection("requests").get().addOnSuccessListener(query -> {
            int pending = 0, approved = 0, declined = 0;

            for (DocumentSnapshot doc : query) {
                String status = doc.getString("status");
                if ("pending".equalsIgnoreCase(status)) pending++;
                else if ("approved".equalsIgnoreCase(status)) approved++;
                else if ("declined".equalsIgnoreCase(status)) declined++;
            }

            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, pending));
            entries.add(new BarEntry(1f, approved));
            entries.add(new BarEntry(2f, declined));

            BarDataSet dataSet = new BarDataSet(entries, "Adoption Requests");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.9f);
            barData.setValueTextSize(14f);
            barData.setValueTextColor(Color.BLACK);

            requestBarChart.setData(barData);
            requestBarChart.setFitBars(true);
            requestBarChart.getDescription().setEnabled(false);
            requestBarChart.getAxisRight().setEnabled(false);
            requestBarChart.getAxisLeft().setGranularity(1f);
            requestBarChart.getXAxis().setGranularity(1f);

            requestBarChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    switch (index) {
                        case 0: return "Pending";
                        case 1: return "Approved";
                        case 2: return "Declined";
                        default: return "";
                    }
                }
            });

            requestBarChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            requestBarChart.animateY(1200);
            requestBarChart.invalidate();
        });
    }
}
