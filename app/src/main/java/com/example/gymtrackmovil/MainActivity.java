package com.example.gymtrackmovil;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.utils.Logger;
import android.widget.TextView;
import com.example.gymtrackmovil.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private SessionManager session;
    private TextView tvUserInitials, tvWelcomeHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.init(this);
        session = new SessionManager(this);
        tvUserInitials = findViewById(R.id.tvUserInitials);
        tvWelcomeHeader = findViewById(R.id.tvWelcomeHeader);
        updateDynamicUI();
        findViewById(R.id.navHome).setOnClickListener(v -> {
        });
        findViewById(R.id.navRoutine).setOnClickListener(v -> startActivity(new Intent(this, RoutinesActivity.class)));
        findViewById(R.id.navProgress).setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        findViewById(R.id.navGoals).setOnClickListener(v -> startActivity(new Intent(this, GoalsActivity.class)));
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        tvUserInitials.setOnClickListener(v -> showLogoutConfirmDialog());

        // Statistics shortcut (accessible from welcome card if present in layout)
        android.view.View statsBtn = findViewById(R.id.btnViewStats);
        if (statsBtn != null) {
            statsBtn.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
        }
    }

    private void updateDynamicUI() {
        String name = session.getUserName();
        tvWelcomeHeader.setText(getString(R.string.welcome_msg, name));
        String initials;
        if (name.contains(" ")) {
            String[] parts = name.split(" ");
            initials = "" + parts[0].charAt(0) + parts[1].charAt(0);
        } else {
            initials = name.substring(0, Math.min(name.length(), 2));
        }
        tvUserInitials.setText(initials.toUpperCase());
    }

    private void showLogoutConfirmDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    session.logoutUser();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
