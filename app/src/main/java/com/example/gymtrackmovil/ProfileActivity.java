package com.example.gymtrackmovil;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.utils.SessionManager;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;
import com.example.gymtrackmovil.api.ApiClient;
import com.example.gymtrackmovil.api.ApiService;
import com.example.gymtrackmovil.models.ProfileUpdateRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etLastName, etEmail, etPhone, etAddress;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        etName = findViewById(R.id.etProfileName);
        etLastName = findViewById(R.id.etProfileLastName);
        etEmail = findViewById(R.id.etProfileEmail);
        etPhone = findViewById(R.id.etProfilePhone);
        etAddress = findViewById(R.id.etProfileAddress);

        loadUserData();

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfileChanges());

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.navRoutine).setOnClickListener(v -> {
            startActivity(new Intent(this, RoutinesActivity.class));
            finish();
        });

        findViewById(R.id.navProgress).setOnClickListener(v -> {
            startActivity(new Intent(this, ProgressActivity.class));
            finish();
        });

        findViewById(R.id.navGoals).setOnClickListener(v -> {
            startActivity(new Intent(this, GoalsActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        String fullName = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();

        if (fullName.contains(" ")) {
            String[] parts = fullName.split(" ", 2);
            etName.setText(parts[0]);
            etLastName.setText(parts[1]);
        } else {
            etName.setText(fullName);
        }
        etEmail.setText(email);
        // Phone and Address would normally come from a getProfile API call
        // For now, we'll leave them empty or use placeholder
    }

    private void saveProfileChanges() {
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Nombre y Apellido son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        ProfileUpdateRequest request = new ProfileUpdateRequest(name, lastName, phone, address);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        apiService.updateProfile(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    sessionManager.createLoginSession(sessionManager.getUserEmail(), name + " " + lastName,
                            sessionManager.getUserToken(), sessionManager.getUserRole());
                } else {
                    String errorMsg = "Error: " + response.code();
                    try (ResponseBody errorBody = response.errorBody()) {
                        if (errorBody != null) {
                            errorMsg += " - " + errorBody.string();
                        }
                    } catch (Exception e) {
                        com.example.gymtrackmovil.utils.Logger.e("Error reading error body", e);
                    }
                    Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Falla red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
