package com.example.gymtrackmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.SessionManager;

import java.util.Locale;

public class TrainerProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String trainerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_profile);

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        trainerEmail = sessionManager.getUserEmail();

        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        tvUserInitials.setOnClickListener(v -> showLogoutDialog(sessionManager));

        findViewById(R.id.btnEditarPerfil).setOnClickListener(v ->
                startActivity(new Intent(this, TrainerEditProfileActivity.class)));

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v ->
                showLogoutDialog(sessionManager));

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrainerData();
    }

    private void loadTrainerData() {
        if (trainerEmail == null || trainerEmail.isEmpty()) {
            return;
        }

        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        TextView tvProfileAvatar = findViewById(R.id.tvProfileAvatar);
        TextView tvProfileName = findViewById(R.id.tvProfileName);
        TextView tvProfileEmail = findViewById(R.id.tvProfileEmail);
        TextView tvProfilePhone = findViewById(R.id.tvProfilePhone);
        TextView tvProfileLocation = findViewById(R.id.tvProfileLocation);
        TextView tvProfileExperience = findViewById(R.id.tvProfileExperience);
        TextView tvProfileSpecialty = findViewById(R.id.tvProfileSpecialty);
        TextView tvProfileStatClientes = findViewById(R.id.tvProfileStatClientes);
        TextView tvProfileStatRating = findViewById(R.id.tvProfileStatRating);
        TextView tvProfileStatSesiones = findViewById(R.id.tvProfileStatSesiones);

        Cursor userCursor = dbHelper.getUserByEmail(trainerEmail);
        String name = "";
        String email = trainerEmail;
        String phone = "—";

        if (userCursor != null) {
            if (userCursor.moveToFirst()) {
                int nameIdx = userCursor.getColumnIndex(DatabaseHelper.KEY_USER_NAME);
                int emailIdx = userCursor.getColumnIndex(DatabaseHelper.KEY_USER_EMAIL);
                int phoneIdx = userCursor.getColumnIndex(DatabaseHelper.KEY_USER_PHONE);

                if (nameIdx != -1) name = userCursor.getString(nameIdx);
                if (emailIdx != -1) email = userCursor.getString(emailIdx);
                if (phoneIdx != -1) {
                    String p = userCursor.getString(phoneIdx);
                    if (p != null && !p.isEmpty()) phone = p;
                }
            }
            userCursor.close();
        }

        if (name == null) name = "";

        if (!name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            String upper = initials.toUpperCase();
            tvUserInitials.setText(upper);
            tvProfileAvatar.setText(upper);
        }
        tvProfileName.setText(name.isEmpty() ? "—" : name);
        tvProfileEmail.setText(email);
        tvProfilePhone.setText(phone);

        Cursor profileCursor = dbHelper.getTrainerProfile(trainerEmail);
        String specialty = "Sin especialidad asignada";
        int experienceYears = 0;
        String certifications = "";
        String location = "Sin ubicación registrada";
        double rating = 0.0;

        if (profileCursor != null) {
            if (profileCursor.moveToFirst()) {
                int specialtyIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_SPECIALTY);
                int expIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_EXPERIENCE_YEARS);
                int certIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_CERTIFICATIONS);
                int locIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_LOCATION);
                int ratingIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_RATING);

                if (specialtyIdx != -1) {
                    String s = profileCursor.getString(specialtyIdx);
                    if (s != null && !s.isEmpty()) specialty = s;
                }
                if (expIdx != -1) experienceYears = profileCursor.getInt(expIdx);
                if (certIdx != -1) {
                    String c = profileCursor.getString(certIdx);
                    if (c != null) certifications = c;
                }
                if (locIdx != -1) {
                    String l = profileCursor.getString(locIdx);
                    if (l != null && !l.isEmpty()) location = l;
                }
                if (ratingIdx != -1) rating = profileCursor.getDouble(ratingIdx);
            }
            profileCursor.close();
        }

        tvProfileSpecialty.setText(specialty);
        tvProfileLocation.setText(location);
        tvProfileExperience.setText(experienceYears + (experienceYears == 1 ? " año experiencia" : " años experiencia"));
        tvProfileStatRating.setText(rating > 0 ? String.format(Locale.getDefault(), "%.1f", rating) : "—");

        setupCertifications(certifications);

        int totalClients = dbHelper.countClientsByTrainer(trainerEmail);
        tvProfileStatClientes.setText(String.valueOf(totalClients));

        int totalSessions = dbHelper.countCompletedSessionsByTrainer(trainerEmail);
        tvProfileStatSesiones.setText(String.valueOf(totalSessions));
    }

    private void setupCertifications(String certifications) {
        View llCert1 = findViewById(R.id.llCert1);
        View llCert2 = findViewById(R.id.llCert2);
        View llCert3 = findViewById(R.id.llCert3);
        TextView tvCert1 = findViewById(R.id.tvCert1);
        TextView tvCert2 = findViewById(R.id.tvCert2);
        TextView tvCert3 = findViewById(R.id.tvCert3);

        View[] views = { llCert1, llCert2, llCert3 };
        TextView[] texts = { tvCert1, tvCert2, tvCert3 };

        if (certifications == null || certifications.trim().isEmpty()) {
            for (int i = 0; i < views.length; i++) {
                if (i == 0) {
                    texts[0].setText("Sin certificaciones registradas");
                    views[0].setVisibility(View.VISIBLE);
                } else {
                    views[i].setVisibility(View.GONE);
                }
            }
            return;
        }

        String[] parts = certifications.split(",");
        for (int i = 0; i < views.length; i++) {
            if (i < parts.length && !parts[i].trim().isEmpty()) {
                texts[i].setText(parts[i].trim());
                views[i].setVisibility(View.VISIBLE);
            } else {
                views[i].setVisibility(View.GONE);
            }
        }
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
