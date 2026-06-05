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
    private com.example.gymtrackmovil.database.DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        session = new SessionManager(this);
        dbHelper = new com.example.gymtrackmovil.database.DatabaseHelper(this);
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
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
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
        List<ProgressEntry> progressList = new java.util.ArrayList<>();
        String email = session.getUserEmail();
        android.database.Cursor cursor = dbHelper.getUserMetrics(email);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int wIdx = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_METRIC_WEIGHT);
                int fIdx = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_METRIC_BODY_FAT);
                int mIdx = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_METRIC_MUSCLE_MASS);
                int dIdx = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_METRIC_DATE);
                double weight = wIdx != -1 ? cursor.getDouble(wIdx) : 0;
                double fat = fIdx != -1 ? cursor.getDouble(fIdx) : 0;
                double muscle = mIdx != -1 ? cursor.getDouble(mIdx) : 0;
                String date = dIdx != -1 ? cursor.getString(dIdx) : "";
                progressList.add(new ProgressEntry(weight, fat, muscle, date));
            }
            cursor.close();
        }
        updateProgressUI(progressList);
    }
    private void updateProgressUI(List<ProgressEntry> progressList) {
        Logger.i("Loaded local progress entries: " + progressList.size());
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
                String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
                saveProgress(new ProgressEntry(weight, fat, muscle, currentDate));
            } catch (Exception e) {
                Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    private void saveProgress(ProgressEntry entry) {
        String email = session.getUserEmail();
        long id = dbHelper.saveMetric(email, entry.getWeight(), entry.getBodyFat(), entry.getMuscleMass(), entry.getDate());
        if (id != -1) {
            Toast.makeText(ProgressActivity.this, "Medición guardada localmente", Toast.LENGTH_SHORT).show();
            fetchProgressData();
        } else {
            Toast.makeText(ProgressActivity.this, "Error al guardar medición en SQLite", Toast.LENGTH_SHORT).show();
        }
    }
}

