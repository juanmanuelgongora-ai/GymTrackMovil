package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.utils.SessionManager;

public class TrainerEditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_edit_profile);

        SessionManager sessionManager = new SessionManager(this);

        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        TextView tvEditAvatar = findViewById(R.id.tvEditAvatar);

        String name = sessionManager.getUserName();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            String upper = initials.toUpperCase();
            tvUserInitials.setText(upper);
            tvEditAvatar.setText(upper);
        }

        tvUserInitials.setOnClickListener(v -> showLogoutDialog(sessionManager));

        findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            EditText etNombre = findViewById(R.id.etNombre);
            EditText etEmail = findViewById(R.id.etEmail);
            EditText etTelefono = findViewById(R.id.etTelefono);
            EditText etUbicacion = findViewById(R.id.etUbicacion);
            EditText etEspecialidad = findViewById(R.id.etEspecialidad);
            EditText etExperiencia = findViewById(R.id.etExperiencia);

            if (etNombre.getText().toString().trim().isEmpty()) {
                etNombre.setError("El nombre es obligatorio");
                return;
            }
            if (etEmail.getText().toString().trim().isEmpty()) {
                etEmail.setError("El email es obligatorio");
                return;
            }

            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());

        findViewById(R.id.btnCambiarContrasena).setOnClickListener(v ->
                Toast.makeText(this, "Cambiar contraseña", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnEliminarCuenta).setOnClickListener(v ->
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Eliminar Cuenta")
                        .setMessage("Esta acción es irreversible. ¿Estás seguro?")
                        .setPositiveButton("Eliminar", (dialog, which) ->
                                Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_SHORT).show())
                        .setNegativeButton("Cancelar", null)
                        .show());

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
