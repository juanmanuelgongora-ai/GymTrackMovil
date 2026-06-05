package com.example.gymtrackmovil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.utils.SessionManager;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;
import com.example.gymtrackmovil.models.ProfileUpdateRequest;
import okhttp3.ResponseBody;
public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etLastName, etEmail, etPhone, etAddress;
    private SessionManager sessionManager;
    private com.example.gymtrackmovil.database.DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sessionManager = new SessionManager(this);
        dbHelper = new com.example.gymtrackmovil.database.DatabaseHelper(this);
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
        String email = sessionManager.getUserEmail();
        etEmail.setText(email);
        android.database.Cursor cursor = dbHelper.getUserByEmail(email);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_USER_NAME);
                int phoneIndex = cursor
                        .getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_USER_PHONE);
                int addressIndex = cursor
                        .getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_USER_ADDRESS);
                String fullName = nameIndex != -1 ? cursor.getString(nameIndex) : sessionManager.getUserName();
                String phone = phoneIndex != -1 ? cursor.getString(phoneIndex) : "";
                String address = addressIndex != -1 ? cursor.getString(addressIndex) : "";
                if (fullName != null && fullName.contains(" ")) {
                    String[] parts = fullName.split(" ", 2);
                    etName.setText(parts[0]);
                    etLastName.setText(parts[1]);
                } else {
                    etName.setText(fullName);
                    etLastName.setText("");
                }
                etPhone.setText(phone);
                etAddress.setText(address);
            }
            cursor.close();
        } else {
            String fullName = sessionManager.getUserName();
            if (fullName.contains(" ")) {
                String[] parts = fullName.split(" ", 2);
                etName.setText(parts[0]);
                etLastName.setText(parts[1]);
            } else {
                etName.setText(fullName);
            }
        }
    }
    private void saveProfileChanges() {
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String fullName = name + " " + lastName;
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        if (name.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Nombre y Apellido son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = sessionManager.getUserEmail();
        int rows = dbHelper.updateUserProfile(email, fullName, address, phone);
        if (rows > 0) {
            Toast.makeText(ProfileActivity.this, "Perfil actualizado correctamente en SQLite", Toast.LENGTH_SHORT)
                    .show();
            sessionManager.createLoginSession(email, fullName, sessionManager.getUserToken(),
                    sessionManager.getUserRole());
        } else {
            Toast.makeText(ProfileActivity.this, "Error al actualizar perfil en SQLite", Toast.LENGTH_SHORT).show();
        }
    }
}


