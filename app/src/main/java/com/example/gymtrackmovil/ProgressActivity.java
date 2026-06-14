package com.example.gymtrackmovil;

import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.api.ApiClient;
import com.example.gymtrackmovil.api.ApiService;
import com.example.gymtrackmovil.models.ProgressEntry;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressActivity extends AppCompatActivity {

    private SessionManager session;
    private TextView tvUserInitials;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        session = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);
        tvUserInitials = findViewById(R.id.tvUserInitials);

        setupUI();
        fetchProgressData();

        findViewById(R.id.btnRegisterProgress).setOnClickListener(v -> showAddProgressDialog());
    }

    private void setupUI() {
        String name = session.getUserName();
        if (name.contains(" ")) {
            String[] parts = name.split(" ");
            String initials = "" + parts[0].charAt(0) + parts[1].charAt(0);
            tvUserInitials.setText(initials.toUpperCase());
        } else {
            tvUserInitials.setText(name.substring(0, Math.min(name.length(), 2)).toUpperCase());
        }

        // Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.navRoutine).setOnClickListener(v -> {
            startActivity(new Intent(this, RoutinesActivity.class));
            finish();
        });

        findViewById(R.id.navGoals).setOnClickListener(v -> {
            startActivity(new Intent(this, GoalsActivity.class));
            finish();
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private void fetchProgressData() {
        apiService.getProgress().enqueue(new Callback<List<ProgressEntry>>() {
            @Override
            public void onResponse(Call<List<ProgressEntry>> call, Response<List<ProgressEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateProgressUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ProgressEntry>> call, Throwable t) {
                Toast.makeText(ProgressActivity.this, "Error al cargar progreso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressUI(List<ProgressEntry> progressList) {
        if (progressList.isEmpty())
            return;

        // Assuming list is sorted by date descending, get latest
        ProgressEntry latest = progressList.get(0);

        // Update Weight Card
        View cardWeight = findViewById(R.id.cardWeight);
        ((TextView) cardWeight.findViewById(R.id.tvStatLabel)).setText("Peso (kg)");
        ((TextView) cardWeight.findViewById(R.id.tvStatValue)).setText(String.format("%.1f", latest.getWeight()));
        ((android.widget.ImageView) cardWeight.findViewById(R.id.ivStatIcon))
                .setImageResource(android.R.drawable.ic_menu_sort_by_size);

        // Update BMI Card
        View cardBMI = findViewById(R.id.cardBMI);
        ((TextView) cardBMI.findViewById(R.id.tvStatLabel)).setText("IMC");
        double h = latest.getHeight() > 0 ? latest.getHeight() / 100.0 : 1.70;
        double bmi = latest.getWeight() / (h * h);
        ((TextView) cardBMI.findViewById(R.id.tvStatValue)).setText(String.format("%.1f", bmi));
        ((android.widget.ImageView) cardBMI.findViewById(R.id.ivStatIcon))
                .setImageResource(android.R.drawable.ic_menu_info_details);

        // Update Fat Card
        View cardFat = findViewById(R.id.cardFat);
        ((TextView) cardFat.findViewById(R.id.tvStatLabel)).setText("Grasa Body (%)");
        ((TextView) cardFat.findViewById(R.id.tvStatValue)).setText(String.format("%.1f%%", latest.getBodyFat()));
        ((android.widget.ImageView) cardFat.findViewById(R.id.ivStatIcon))
                .setImageResource(android.R.drawable.ic_menu_view);

        // Update Muscle Card
        View cardMuscle = findViewById(R.id.cardMuscle);
        ((TextView) cardMuscle.findViewById(R.id.tvStatLabel)).setText("Músculo (%)");
        ((TextView) cardMuscle.findViewById(R.id.tvStatValue)).setText(String.format("%.1f%%", latest.getMuscleMass()));
        ((android.widget.ImageView) cardMuscle.findViewById(R.id.ivStatIcon))
                .setImageResource(android.R.drawable.ic_menu_compass);
    }

    private void showAddProgressDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nueva Medición");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_progress, null);
        final EditText etWeight = view.findViewById(R.id.etDialogWeight);
        final EditText etBodyFat = view.findViewById(R.id.etDialogBodyFat);
        final EditText etMuscleMass = view.findViewById(R.id.etDialogMuscleMass);

        builder.setView(view);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            try {
                double weight = Double.parseDouble(etWeight.getText().toString());
                double fat = Double.parseDouble(etBodyFat.getText().toString());
                double muscle = Double.parseDouble(etMuscleMass.getText().toString());

                String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(new java.util.Date());
                ProgressEntry entry = new ProgressEntry(weight, 170.0, fat, muscle, currentDate);
                saveProgress(entry);
            } catch (Exception e) {
                Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void saveProgress(ProgressEntry entry) {
        apiService.addProgress(entry).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProgressActivity.this, "Medición guardada", Toast.LENGTH_SHORT).show();
                    fetchProgressData();
                } else {
                    Toast.makeText(ProgressActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProgressActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
