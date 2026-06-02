package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;

public class AdminMembersActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_admin_members);

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

                RecyclerView rvMembers = findViewById(R.id.rvMembers);
                rvMembers.setLayoutManager(new LinearLayoutManager(this));

                java.util.List<com.example.gymtrackmovil.adapters.AdminMembersAdapter.Member> mockMembers = new java.util.ArrayList<>();
                mockMembers.add(new com.example.gymtrackmovil.adapters.AdminMembersAdapter.Member("Juan García",
                                "juan@email.com", "Premium", "Activo"));
                mockMembers.add(new com.example.gymtrackmovil.adapters.AdminMembersAdapter.Member("María López",
                                "maria@email.com", "Estándar", "Activo"));
                mockMembers.add(new com.example.gymtrackmovil.adapters.AdminMembersAdapter.Member("Carlos Ruiz",
                                "carlos@email.com", "Básico", "Inactivo"));
                mockMembers.add(new com.example.gymtrackmovil.adapters.AdminMembersAdapter.Member("Ana Martínez",
                                "ana@email.com", "Premium", "Activo"));

                rvMembers.setAdapter(new com.example.gymtrackmovil.adapters.AdminMembersAdapter(mockMembers));

                findViewById(R.id.navAdminHome).setOnClickListener(v -> {
                        startActivity(new Intent(this, AdminDashboardActivity.class));
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
