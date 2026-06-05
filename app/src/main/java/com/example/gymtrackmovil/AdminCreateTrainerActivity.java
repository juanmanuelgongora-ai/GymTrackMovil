package com.example.gymtrackmovil;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.Logger;
public class AdminCreateTrainerActivity extends AppCompatActivity {
    private EditText etTrainerName, etTrainerEmail, etTrainerPassword;
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_trainer);
        Logger.init(this);
        dbHelper = new DatabaseHelper(this);
        etTrainerName     = findViewById(R.id.etTrainerName);
        etTrainerEmail    = findViewById(R.id.etTrainerEmail);
        etTrainerPassword = findViewById(R.id.etTrainerPassword);
        findViewById(R.id.btnSaveTrainer).setOnClickListener(v -> saveTrainer());
        findViewById(R.id.btnCancelTrainer).setOnClickListener(v -> finish());
        View back = findViewById(R.id.ivBack);
        if (back != null) back.setOnClickListener(v -> finish());
    }
    private void saveTrainer() {
        String name     = etTrainerName.getText().toString().trim();
        String email    = etTrainerEmail.getText().toString().trim();
        String password = etTrainerPassword.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper.checkUserExists(email)) {
            Toast.makeText(this, "Ya existe un usuario con ese correo", Toast.LENGTH_LONG).show();
            return;
        }
        long id = dbHelper.saveUserWithRole(
                name, email, password, "Entrenamiento",
                "", 0, "", "", "",
                0.0, 0.0, "", "entrenador"
        );
        if (id != -1) {
            Logger.i("Entrenador creado: " + email);
            Toast.makeText(this, "✅ Entrenador \"" + name + "\" creado exitosamente", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al crear el entrenador", Toast.LENGTH_SHORT).show();
        }
    }
}


