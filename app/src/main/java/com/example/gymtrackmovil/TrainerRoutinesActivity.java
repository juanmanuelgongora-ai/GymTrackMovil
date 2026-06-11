package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TrainerRoutinesActivity extends AppCompatActivity {

    static class TrainerRoutine {
        String title;
        int exerciseCount;
        int durationMin;
        int clientCount;

        TrainerRoutine(String title, int exerciseCount, int durationMin, int clientCount) {
            this.title = title;
            this.exerciseCount = exerciseCount;
            this.durationMin = durationMin;
            this.clientCount = clientCount;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_routines);

        SessionManager sessionManager = new SessionManager(this);

        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        String name = sessionManager.getUserName();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            tvUserInitials.setText(initials.toUpperCase());
        }

        tvUserInitials.setOnClickListener(v -> showLogoutDialog(sessionManager));

        setupRoutineList();

        findViewById(R.id.tvAddRoutine).setOnClickListener(v ->
                Toast.makeText(this, "Crear nueva rutina", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnCrearRutina).setOnClickListener(v ->
                Toast.makeText(this, "Crear nueva rutina", Toast.LENGTH_SHORT).show());

        setupBottomNav();
    }

    private void setupRoutineList() {
        List<TrainerRoutine> routines = new ArrayList<>();
        routines.add(new TrainerRoutine("Fuerza - Tren Superior", 8, 45, 12));
        routines.add(new TrainerRoutine("Cardio Intensivo", 6, 30, 8));
        routines.add(new TrainerRoutine("Full Body Principiantes", 10, 40, 15));
        routines.add(new TrainerRoutine("HIIT Avanzado", 12, 35, 6));
        routines.add(new TrainerRoutine("Piernas & Glúteos", 9, 50, 10));

        LinearLayout llRoutineList = findViewById(R.id.llRoutineList);
        llRoutineList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (TrainerRoutine routine : routines) {
            View card = inflater.inflate(R.layout.item_trainer_routine_card, llRoutineList, false);

            ((TextView) card.findViewById(R.id.tvRoutineTitle)).setText(routine.title);
            ((TextView) card.findViewById(R.id.tvExerciseCount)).setText(routine.exerciseCount + " ejercicios");
            ((TextView) card.findViewById(R.id.tvDuration)).setText(routine.durationMin + " min");
            ((TextView) card.findViewById(R.id.tvUsedByClients)).setText("Usada por " + routine.clientCount + " clientes");

            card.findViewById(R.id.tvVerDetalles).setOnClickListener(v ->
                    Toast.makeText(this, routine.title, Toast.LENGTH_SHORT).show());

            llRoutineList.addView(card);
        }

        ((TextView) findViewById(R.id.tvRoutineCount)).setText(routines.size() + " rutinas creadas");
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.navClientes).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerClientsActivity.class));
            finish();
        });

        findViewById(R.id.navPerfil).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
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
