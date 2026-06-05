package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.R;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.NetworkUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private com.example.gymtrackmovil.utils.SessionManager sessionManager;
    private com.example.gymtrackmovil.database.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Logger.init(this);
        sessionManager = new com.example.gymtrackmovil.utils.SessionManager(this);
        dbHelper = new com.example.gymtrackmovil.database.DatabaseHelper(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getUserRole();
            if (role != null && (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("administrador"))) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.tvLogo).setOnLongClickListener(v -> {
            showServerSettingsDialog();
            return true;
        });

        findViewById(R.id.btnLogin).setOnClickListener(v -> handleLogin());
        findViewById(R.id.tvGoToRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Admin hardcoded bypass for testing admin dashboard
        boolean isValid = false;
        String role = "cliente";
        String name = "Usuario GymTrack";

        if (email.equalsIgnoreCase("admin@gymtrack.com") && password.equals("admin123")) {
            isValid = true;
            role = "admin";
            name = "Administrador Gym";
        } else {
            isValid = dbHelper.checkUserCredentials(email, password);
            if (isValid) {
                android.database.Cursor cursor = dbHelper.getUserByEmail(email);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_USER_NAME);
                        if (nameIndex != -1) {
                            name = cursor.getString(nameIndex);
                        }
                        int goalIndex = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_USER_GOAL);
                        if (goalIndex != -1) {
                            String goal = cursor.getString(goalIndex);
                            if (goal != null && goal.equalsIgnoreCase("admin")) {
                                role = "admin";
                            }
                        }
                    }
                    cursor.close();
                }
            }
        }

        if (isValid) {
            String token = "local-session-token-" + email;
            sessionManager.createLoginSession(email, name, token, role);
            
            Logger.i("Login exitoso local para: " + email + " con rol: [" + role + "]");
            Toast.makeText(LoginActivity.this, "Bienvenido " + name, Toast.LENGTH_SHORT).show();

            if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("administrador")) {
                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
            } else if (role.equalsIgnoreCase("entrenador") || role.equalsIgnoreCase("trainer")) {
                startActivity(new Intent(LoginActivity.this, TrainerDashboardActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            finish();
        } else {
            Logger.e("Login fallido local: Credenciales incorrectas para " + email, null);
            Toast.makeText(LoginActivity.this, "Error: Credenciales inválidas", Toast.LENGTH_SHORT).show();
        }
    }


    private void showServerSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Configuración del Servidor");
        builder.setMessage("Ingrese la IP y puerto del servidor (ej: 192.168.1.4:8000)");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        input.setText(sessionManager.getServerIp());
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newIp = input.getText().toString().trim();
            if (!newIp.isEmpty()) {
                sessionManager.updateServerIp(newIp);
                Toast.makeText(this, "Servidor actualizado a: " + newIp, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
