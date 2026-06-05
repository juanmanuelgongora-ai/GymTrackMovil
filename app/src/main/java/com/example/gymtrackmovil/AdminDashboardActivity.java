package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;
import android.widget.TextView;

public class AdminDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Logger.init(this);
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        // Header initials
        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        String name = sessionManager.getUserName();
        if (name != null) {
            String initials;
            if (name.contains(" ")) {
                String[] parts = name.split(" ");
                initials = "" + parts[0].charAt(0) + (parts.length > 1 ? parts[1].charAt(0) : "");
            } else {
                initials = name.substring(0, Math.min(name.length(), 2));
            }
            tvUserInitials.setText(initials.toUpperCase());
        }
        tvUserInitials.setOnClickListener(v -> showLogoutConfirmDialog());

        // Load real stats from SQLite
        loadStats();

        // Navigation
        findViewById(R.id.navAdminHome).setOnClickListener(v -> { /* already here */ });
        findViewById(R.id.navAdminMembers).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminMembersActivity.class));
        });
        findViewById(R.id.navAdminTrainers).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminCreateTrainerActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        int totalUsers    = dbHelper.countAllUsers();
        int totalClients  = dbHelper.countUsersByRole("cliente");
        int totalTrainers = dbHelper.countUsersByRole("entrenador");

        // Update stat cards via child-view traversal (matching item_admin_stat layout)
        android.widget.GridLayout grid = findViewById(R.id.glAdminStats);
        if (grid != null && grid.getChildCount() >= 4) {
            setStatCard(grid.getChildAt(0), "Total Usuarios",    String.valueOf(totalUsers),    "registrados");
            setStatCard(grid.getChildAt(1), "Clientes",          String.valueOf(totalClients),  "activos");
            setStatCard(grid.getChildAt(2), "Entrenadores",      String.valueOf(totalTrainers), "en planta");
            setStatCard(grid.getChildAt(3), "Sesiones Hoy",      "0",                           "en curso");
        }
    }

    private void setStatCard(android.view.View card, String label, String value, String diff) {
        if (card == null) return;
        TextView tvLabel = card.findViewById(R.id.tvAdminStatLabel);
        TextView tvValue = card.findViewById(R.id.tvAdminStatValue);
        TextView tvDiff  = card.findViewById(R.id.tvAdminStatDiff);
        if (tvLabel != null) tvLabel.setText(label);
        if (tvValue != null) tvValue.setText(value);
        if (tvDiff  != null) tvDiff.setText(diff);
    }

    private void showLogoutConfirmDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    sessionManager.logoutUser();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
