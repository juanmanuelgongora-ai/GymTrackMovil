package com.example.gymtrackmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.SessionManager;

public class TrainerEditProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String trainerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_edit_profile);

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        trainerEmail = sessionManager.getUserEmail();

        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        tvUserInitials.setOnClickListener(v -> showLogoutDialog(sessionManager));

        loadCurrentData();

        findViewById(R.id.btnGuardar).setOnClickListener(v -> saveProfile());
        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
        findViewById(R.id.btnCambiarContrasena).setOnClickListener(v -> showChangePasswordDialog());

        findViewById(R.id.btnEliminarCuenta).setOnClickListener(v ->
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Eliminar Cuenta")
                        .setMessage("Esta acción es irreversible. ¿Estás seguro?")
                        .setPositiveButton("Eliminar", (dialog, which) ->
                                Toast.makeText(this, "Funcionalidad no disponible", Toast.LENGTH_SHORT).show())
                        .setNegativeButton("Cancelar", null)
                        .show());

        setupBottomNav();
    }

    private void loadCurrentData() {
        TextView tvEditAvatar = findViewById(R.id.tvEditAvatar);
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etTelefono = findViewById(R.id.etTelefono);
        EditText etUbicacion = findViewById(R.id.etUbicacion);
        EditText etEspecialidad = findViewById(R.id.etEspecialidad);
        EditText etExperiencia = findViewById(R.id.etExperiencia);
        EditText etCertificaciones = findViewById(R.id.etCertificaciones);
        EditText etDescripcion = findViewById(R.id.etDescripcion);

        if (trainerEmail == null || trainerEmail.isEmpty()) {
            return;
        }

        Cursor userCursor = dbHelper.getUserByEmail(trainerEmail);
        String name = "";
        if (userCursor != null) {
            if (userCursor.moveToFirst()) {
                int nameIdx = userCursor.getColumnIndex(DatabaseHelper.KEY_USER_NAME);
                int phoneIdx = userCursor.getColumnIndex(DatabaseHelper.KEY_USER_PHONE);

                if (nameIdx != -1) name = userCursor.getString(nameIdx);
                if (phoneIdx != -1) {
                    String phone = userCursor.getString(phoneIdx);
                    etTelefono.setText(phone != null ? phone : "");
                }
            }
            userCursor.close();
        }

        etNombre.setText(name != null ? name : "");
        etEmail.setText(trainerEmail);
        etEmail.setEnabled(false);
        etEmail.setFocusable(false);

        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            String upper = initials.toUpperCase();
            tvUserInitialsSafe().setText(upper);
            tvEditAvatar.setText(upper);
        }

        Cursor profileCursor = dbHelper.getTrainerProfile(trainerEmail);
        if (profileCursor != null) {
            if (profileCursor.moveToFirst()) {
                int specialtyIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_SPECIALTY);
                int expIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_EXPERIENCE_YEARS);
                int certIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_CERTIFICATIONS);
                int descIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_DESCRIPTION);
                int locIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_LOCATION);

                if (specialtyIdx != -1) etEspecialidad.setText(profileCursor.getString(specialtyIdx));
                if (expIdx != -1) etExperiencia.setText(String.valueOf(profileCursor.getInt(expIdx)));
                if (certIdx != -1) etCertificaciones.setText(profileCursor.getString(certIdx));
                if (descIdx != -1) etDescripcion.setText(profileCursor.getString(descIdx));
                if (locIdx != -1) etUbicacion.setText(profileCursor.getString(locIdx));
            }
            profileCursor.close();
        }
    }

    private TextView tvUserInitialsSafe() {
        return findViewById(R.id.tvUserInitials);
    }

    private void saveProfile() {
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etTelefono = findViewById(R.id.etTelefono);
        EditText etUbicacion = findViewById(R.id.etUbicacion);
        EditText etEspecialidad = findViewById(R.id.etEspecialidad);
        EditText etExperiencia = findViewById(R.id.etExperiencia);
        EditText etCertificaciones = findViewById(R.id.etCertificaciones);
        EditText etDescripcion = findViewById(R.id.etDescripcion);

        String name = etNombre.getText().toString().trim();
        String phone = etTelefono.getText().toString().trim();
        String location = etUbicacion.getText().toString().trim();
        String specialty = etEspecialidad.getText().toString().trim();
        String experienceStr = etExperiencia.getText().toString().trim();
        String certifications = etCertificaciones.getText().toString().trim();
        String description = etDescripcion.getText().toString().trim();

        if (name.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            return;
        }

        int experienceYears = 0;
        if (!experienceStr.isEmpty()) {
            try {
                experienceYears = Integer.parseInt(experienceStr);
                if (experienceYears < 0) {
                    etExperiencia.setError("El valor debe ser positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                etExperiencia.setError("Ingresa un número válido");
                return;
            }
        }

        dbHelper.updateTrainerBasicInfo(trainerEmail, name, phone, location);
        dbHelper.saveTrainerProfile(trainerEmail, specialty, experienceYears, certifications, description, location);

        if (!name.equals(sessionManager.getUserName())) {
            sessionManager.updateUserName(name);
        }

        Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Cambiar Contraseña")
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String current = etCurrentPassword.getText().toString();
            String newPass = etNewPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();

            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPass.length() < 6) {
                Toast.makeText(this, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.updateUserPassword(trainerEmail, current, newPass);
            if (success) {
                Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
            }
        });
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

        findViewById(R.id.navPerfil).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerProfileActivity.class));
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
