package com.example.gymtrackmovil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.utils.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * StatisticsActivity
 *
 * Displays two charts relevant to GymTrack data:
 * 1. BarChart — Sesiones de entrenamiento por semana
 * 2. LineChart — Evolución del peso corporal (últimas semanas)
 *
 * Data is seeded locally to simulate realistic values applicable
 * to the project (can later be fetched from the API/DB).
 */
public class StatisticsActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private BarChart barChart;
    private LineChart lineChart;
    private TextView tvUserInitials;

    // ---- Color palette (orange branding) ----
    private static final int ORANGE = Color.parseColor("#FF6B35");
    private static final int ORANGE_LIGHT = Color.parseColor("#FFAE80");
    private static final int WHITE = Color.WHITE;
    private static final int GRID_COLOR = Color.parseColor("#2A2A3E");
    private static final int TEXT_COLOR = Color.parseColor("#AAAACC");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sessionManager = new SessionManager(this);
        tvUserInitials = findViewById(R.id.tvUserInitials);

        setupHeader();
        setupBarChart();
        setupLineChart();

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    // -------------------------------------------------------------------------
    // Header
    // -------------------------------------------------------------------------
    private void setupHeader() {
        String name = sessionManager.getUserName();
        if (name != null && !name.isEmpty()) {
            String initials = name.contains(" ")
                    ? "" + name.charAt(0) + name.split(" ")[1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            tvUserInitials.setText(initials.toUpperCase());
        }
    }

    // -------------------------------------------------------------------------
    // BAR CHART — Weekly training sessions
    // -------------------------------------------------------------------------
    private void setupBarChart() {
        barChart = findViewById(R.id.barChart);

        // --- Data (simulated, applicable to GymTrack) ---
        // Each value = number of sessions that week
        float[] weekSessions = { 3f, 5f, 4f, 6f, 3f, 5f, 7f, 4f };
        String[] weekLabels = { "Sem 1", "Sem 2", "Sem 3", "Sem 4",
                "Sem 5", "Sem 6", "Sem 7", "Sem 8" };

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < weekSessions.length; i++) {
            entries.add(new BarEntry(i, weekSessions[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Sesiones / semana");
        dataSet.setColor(ORANGE);
        dataSet.setValueTextColor(WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setBarBorderRadius(6f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.55f);

        barChart.setData(barData);
        styleBarChart(barChart, weekLabels);
        barChart.invalidate();
    }

    private void styleBarChart(BarChart chart, String[] xLabels) {
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.animateY(800);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(TEXT_COLOR);
        xAxis.setGridColor(GRID_COLOR);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);

        YAxis left = chart.getAxisLeft();
        left.setTextColor(TEXT_COLOR);
        left.setGridColor(GRID_COLOR);
        left.setAxisMinimum(0f);
        left.setDrawAxisLine(false);

        chart.getAxisRight().setEnabled(false);

        Legend legend = chart.getLegend();
        legend.setTextColor(WHITE);
        legend.setTextSize(11f);
    }

    // -------------------------------------------------------------------------
    // LINE CHART — Body weight evolution
    // -------------------------------------------------------------------------
    private void setupLineChart() {
        lineChart = findViewById(R.id.lineChart);

        // Simulated weight data over 8 weeks (kg, realistic downward trend)
        float[] weights = { 86.5f, 85.8f, 85.2f, 84.7f, 84.1f, 83.5f, 83.0f, 82.5f };
        String[] weekLabels = { "Sem 1", "Sem 2", "Sem 3", "Sem 4",
                "Sem 5", "Sem 6", "Sem 7", "Sem 8" };

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < weights.length; i++) {
            entries.add(new Entry(i, weights[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Peso (kg)");
        dataSet.setColor(ORANGE);
        dataSet.setCircleColor(ORANGE_LIGHT);
        dataSet.setCircleHoleColor(Color.parseColor("#1E1E2E"));
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(2.5f);
        dataSet.setLineWidth(2.5f);
        dataSet.setValueTextColor(WHITE);
        dataSet.setValueTextSize(9f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(50);
        dataSet.setFillColor(ORANGE);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        styleLineChart(lineChart, weekLabels);
        lineChart.invalidate();
    }

    private void styleLineChart(LineChart chart, String[] xLabels) {
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.animateX(1000);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(TEXT_COLOR);
        xAxis.setGridColor(GRID_COLOR);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);

        YAxis left = chart.getAxisLeft();
        left.setTextColor(TEXT_COLOR);
        left.setGridColor(GRID_COLOR);
        left.setDrawAxisLine(false);

        chart.getAxisRight().setEnabled(false);

        Legend legend = chart.getLegend();
        legend.setTextColor(WHITE);
        legend.setTextSize(11f);
    }
}
