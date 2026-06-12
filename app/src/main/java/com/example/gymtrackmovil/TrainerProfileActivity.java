package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.utils.SessionManager;

public class TrainerProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_profile);

        SessionManager sessionManager = new SessionManager(this);

        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        TextView tvProfileAvatar = findViewById(R.id.tvProfileAvatar);
        TextView tvProfileName = findViewById(R.id.tvProfileName);

        String name = sessionManager.getUserName();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            String upper = initials.toUpperCase();
            tvUserInitials.setText(upper);
            tvProfileAvatar.setText(upper);
            tvProfileName.setText(name);
        }

        findViewById(R.id.btnEditarPerfil).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerEditProfileActivity.class));
        });

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v ->
                showLogoutDialog(sessionManager));

        tvUserInitials.setOnClickListener(v -> showLogoutDialog(sessionManager));

        setupBottomNav();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerDashboardActivity.class));
            finish();
        });

        findViewById(R.id.navClientes).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerClientsActivity.class));
            finish();
        });

        findViewById(R.id.navRutinas).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerRoutinesActivity.class));
            finish();
        });
    }

    private void showLogoutDialog(SessionManager session) {
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
