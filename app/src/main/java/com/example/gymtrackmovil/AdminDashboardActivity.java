package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;
import android.widget.TextView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Logger.init(this);
        SessionManager sessionManager = new SessionManager(this);

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

        tvUserInitials.setOnClickListener(v -> showLogoutConfirmDialog(sessionManager));

        findViewById(R.id.navAdminMembers).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminMembersActivity.class));
            finish();
        });
    }

    private void showLogoutConfirmDialog(SessionManager session) {
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
