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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Logger.init(this);
        sessionManager = new com.example.gymtrackmovil.utils.SessionManager(this);

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

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Logger.e("Intento de login fallido: Sin conexión a internet", null);
            Toast.makeText(this, "No hay conexión a internet.", Toast.LENGTH_LONG).show();
            return;
        }

        // API Call via Retrofit
        com.example.gymtrackmovil.api.ApiService apiService = com.example.gymtrackmovil.api.ApiClient.getClient(this)
                .create(com.example.gymtrackmovil.api.ApiService.class);
        com.example.gymtrackmovil.models.LoginRequest loginRequest = new com.example.gymtrackmovil.models.LoginRequest(
                email, password);

        apiService.login(loginRequest)
                .enqueue(new retrofit2.Callback<com.example.gymtrackmovil.models.LoginResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.gymtrackmovil.models.LoginResponse> call,
                            retrofit2.Response<com.example.gymtrackmovil.models.LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            com.example.gymtrackmovil.models.LoginResponse loginResponse = response.body();
                            String token = loginResponse.getToken();
                            Logger.i("Token recibido: "
                                    + (token != null
                                            ? token.substring(0, Math.min(token.length(), 10)) + "... (long: "
                                                    + token.length() + ")"
                                            : "NULL"));

                            String role = loginResponse.getUser().getRole();
                            sessionManager.createLoginSession(loginResponse.getUser().getEmail(),
                                    loginResponse.getUser().getName(),
                                    token,
                                    role);

                            Logger.i("Login exitoso para: " + email + " con rol: [" + (role != null ? role : "null")
                                    + "]");
                            Toast.makeText(LoginActivity.this,
                                    "Bienvenido " + loginResponse.getUser().getName(),
                                    Toast.LENGTH_SHORT).show();

                            if (role != null
                                    && (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("administrador"))) {
                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            finish();
                        } else {
                            Logger.e("Login fallido: Credenciales incorrectas", null);
                            Toast.makeText(LoginActivity.this, "Error: Credenciales inválidas", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.gymtrackmovil.models.LoginResponse> call,
                            Throwable t) {
                        Logger.e("Error en la conexión API Login", t);
                        Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
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
