package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;

public class TrainerDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvUserInitials, tvWelcomeTrainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_dashboard);

        Logger.init(this);
        sessionManager = new SessionManager(this);

        tvUserInitials = findViewById(R.id.tvUserInitials);
        tvWelcomeTrainer = findViewById(R.id.tvWelcomeTrainer);

        setupUI();

        tvUserInitials.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    private void setupUI() {
        String name = sessionManager.getUserName();
        tvWelcomeTrainer.setText("¡Hola, " + name.split(" ")[0] + "!");

        // Initials logic
        String initials;
        if (name.contains(" ")) {
            String[] parts = name.split(" ");
            initials = "" + parts[0].charAt(0) + parts[1].charAt(0);
        } else {
            initials = name.substring(0, Math.min(name.length(), 2));
        }
        tvUserInitials.setText(initials.toUpperCase());

        // Setup mock stats
        updateStatCard(findViewById(R.id.statClients), "12", "Clientes", android.R.drawable.ic_menu_myplaces);
        updateStatCard(findViewById(R.id.statToday), "4", "Hoy", android.R.drawable.ic_menu_today);
        updateStatCard(findViewById(R.id.statRating), "4.8", "Rating", android.R.drawable.btn_star_big_on);
    }

    private void updateStatCard(android.view.View card, String value, String label, int iconRes) {
        ((TextView) card.findViewById(R.id.tvStatValue)).setText(value);
        ((TextView) card.findViewById(R.id.tvStatLabel)).setText(label);
        ((android.widget.ImageView) card.findViewById(R.id.ivStatIcon)).setImageResource(iconRes);
    }

    private void showLogoutConfirmDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir del panel de entrenador?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    sessionManager.logoutUser();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
