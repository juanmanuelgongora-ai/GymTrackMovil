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
import com.example.gymtrackmovil.models.Goal;
import com.example.gymtrackmovil.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalsActivity extends AppCompatActivity {

    private SessionManager session;
    private TextView tvUserInitials;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        session = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);
        tvUserInitials = findViewById(R.id.tvUserInitials);

        setupUI();
        fetchGoals();

        findViewById(R.id.btnCreateGoal).setOnClickListener(v -> showAddGoalDialog());
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

        // Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.navRoutine).setOnClickListener(v -> {
            startActivity(new Intent(this, RoutinesActivity.class));
            finish();
        });

        findViewById(R.id.navProgress).setOnClickListener(v -> {
            startActivity(new Intent(this, ProgressActivity.class));
            finish();
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private void fetchGoals() {
        apiService.getGoals().enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update goals list UI
                }
            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                Toast.makeText(GoalsActivity.this, "Error al cargar objetivos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddGoalDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nuevo Objetivo");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_goal, null);
        final EditText etTitle = view.findViewById(R.id.etGoalTitle);
        final EditText etTarget = view.findViewById(R.id.etGoalTarget);

        builder.setView(view);
        builder.setPositiveButton("Crear", (dialog, which) -> {
            String title = etTitle.getText().toString();
            String target = etTarget.getText().toString();
            if (!title.isEmpty()) {
                saveGoal(new Goal(title, target, 0, "2026-12-31"));
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void saveGoal(Goal goal) {
        apiService.addGoal(goal).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GoalsActivity.this, "Objetivo creado", Toast.LENGTH_SHORT).show();
                    fetchGoals();
                } else {
                    String msg = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null)
                            msg += " " + response.errorBody().string();
                    } catch (Exception e) {
                    }
                    Toast.makeText(GoalsActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(GoalsActivity.this, "Falla red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
