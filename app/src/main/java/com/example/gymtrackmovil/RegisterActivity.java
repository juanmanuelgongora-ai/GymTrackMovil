package com.example.gymtrackmovil;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.R;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.NetworkUtils;

public class RegisterActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private android.widget.ScrollView scrollView;
    private Button btnBack, btnContinue;
    private TextView tvStep1Circle, tvStep2Circle, tvStep3Circle;
    private com.example.gymtrackmovil.database.DatabaseHelper dbHelper;
    private int currentStep = 0;

    // Step 1 Fields
    private EditText etName, etLastName, etAddress, etAge, etEmail, etPassword, etPhone, etFamilyPhone;
    private Spinner spinnerEPS;

    // Step 2 Fields
    private EditText etWeight, etHeight;
    private Spinner spinnerSex;

    // Step 3 Fields
    private Spinner spinnerGoal;
    private CheckBox cbDataTreatment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Logger.init(this);
        dbHelper = new com.example.gymtrackmovil.database.DatabaseHelper(this);

        // UI Components
        viewFlipper = findViewById(R.id.viewFlipper);
        scrollView = findViewById(R.id.scrollView);
        btnBack = findViewById(R.id.btnBack);
        btnContinue = findViewById(R.id.btnContinue);
        tvStep1Circle = findViewById(R.id.tvStep1Circle);
        tvStep2Circle = findViewById(R.id.tvStep2Circle);
        tvStep3Circle = findViewById(R.id.tvStep3Circle);

        // Step 1 Fields
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etAddress = findViewById(R.id.etAddress);
        etAge = findViewById(R.id.etAge);
        etEmail = findViewById(R.id.etEmail);
        spinnerEPS = findViewById(R.id.spinnerEPS);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etFamilyPhone = findViewById(R.id.etFamilyPhone);

        // Step 2 Fields
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        spinnerSex = findViewById(R.id.spinnerSex);

        // Step 3 Fields
        spinnerGoal = findViewById(R.id.spinnerGoal);
        cbDataTreatment = findViewById(R.id.cbDataTreatment);

        // Spinners setup
        setupSpinners();

        // Step 3 Logic: Data Treatment Checkbox
        cbDataTreatment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentStep == 2) {
                updateRegisterButtonState(isChecked);
            }
        });

        // Listeners
        btnContinue.setOnClickListener(v -> handleContinue());
        btnBack.setOnClickListener(v -> handleBack());
        findViewById(R.id.tvGoToLogin).setOnClickListener(v -> finish());

        updateStepperUI();
    }

    private void setupSpinners() {
        String[] epsOptions = { "Seleccione su EPS", "Sura", "Sanitas", "Compensar", "COOMEVA", "Otro" };
        ArrayAdapter<String> epsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, epsOptions);
        epsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerEPS.setAdapter(epsAdapter);

        String[] sexOptions = { "Seleccione sexo", "Masculino", "Femenino", "Otro" };
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, sexOptions);
        sexAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSex.setAdapter(sexAdapter);

        String[] goalOptions = { "Seleccione objetivo", "Perder peso", "Ganar masa muscular", "Mantenerse en forma",
                "Competencia" };
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, goalOptions);
        goalAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGoal.setAdapter(goalAdapter);
    }

    private void handleContinue() {
        if (currentStep == 0 && !validateStep1())
            return;
        if (currentStep == 1 && !validateStep2())
            return;

        if (currentStep < 2) {
            currentStep++;
            viewFlipper.showNext();
            scrollView.smoothScrollTo(0, 0);
            updateStepperUI();
        } else {
            finalizeRegistration();
        }
    }

    private boolean validateStep1() {
        if (etName.getText().toString().isEmpty() || etLastName.getText().toString().isEmpty() ||
                etAddress.getText().toString().isEmpty() ||
                etAge.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() ||
                spinnerEPS.getSelectedItemPosition() == 0 || etPassword.getText().toString().isEmpty() ||
                etPhone.getText().toString().isEmpty() || etFamilyPhone.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos del paso 1", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateStep2() {
        if (etAge.getText().toString().isEmpty() || etWeight.getText().toString().isEmpty() ||
                etHeight.getText().toString().isEmpty() || spinnerSex.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Por favor complete los datos físicos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void handleBack() {
        if (currentStep > 0) {
            currentStep--;
            viewFlipper.showPrevious();
            scrollView.smoothScrollTo(0, 0);
            updateStepperUI();
        } else {
            finish();
        }
    }

    private void updateStepperUI() {
        // Reset colors
        tvStep1Circle.setBackgroundResource(R.drawable.step_circle_inactive);
        tvStep2Circle.setBackgroundResource(R.drawable.step_circle_inactive);
        tvStep3Circle.setBackgroundResource(R.drawable.step_circle_inactive);

        // Highlight current
        if (currentStep == 0) {
            tvStep1Circle.setBackgroundResource(R.drawable.step_circle_active);
            btnBack.setText("Regresar");
            btnContinue.setText("Continuar");
            btnContinue.setEnabled(true);
            btnContinue.setAlpha(1.0f);
        } else if (currentStep == 1) {
            tvStep2Circle.setBackgroundResource(R.drawable.step_circle_active);
            btnBack.setText("Atrás");
            btnContinue.setText("Continuar");
            btnContinue.setEnabled(true);
            btnContinue.setAlpha(1.0f);
        } else if (currentStep == 2) {
            tvStep3Circle.setBackgroundResource(R.drawable.step_circle_active);
            btnBack.setText("Atrás");
            btnContinue.setText("Crear Cuenta");
            updateRegisterButtonState(cbDataTreatment.isChecked());
        }
    }

    private void updateRegisterButtonState(boolean isChecked) {
        btnContinue.setEnabled(isChecked);
        btnContinue.setAlpha(isChecked ? 1.0f : 0.5f);
    }

    private void finalizeRegistration() {
        if (!cbDataTreatment.isChecked()) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Logger.e("Registro fallido: Sin red", null);
            Toast.makeText(this, "Sin conexión a internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collecting data
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String eps = spinnerEPS.getSelectedItem().toString();
        String goal = spinnerGoal.getSelectedItem().toString();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String familyPhone = etFamilyPhone.getText().toString().trim();

        com.example.gymtrackmovil.models.RegisterRequest request = new com.example.gymtrackmovil.models.RegisterRequest(
                name, lastName, email, password, password, // password_confirmation = password
                Integer.parseInt(etAge.getText().toString()),
                spinnerSex.getSelectedItem().toString(),
                eps, goal, address, phone, familyPhone);

        com.example.gymtrackmovil.api.ApiService apiService = com.example.gymtrackmovil.api.ApiClient.getClient(this)
                .create(com.example.gymtrackmovil.api.ApiService.class);

        apiService.register(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Logger.i("Usuario registrado: " + email);
                    int age = Integer.parseInt(etAge.getText().toString());
                    double weightStr = etWeight.getText().toString().isEmpty() ? 0
                            : Double.parseDouble(etWeight.getText().toString());
                    double heightStr = etHeight.getText().toString().isEmpty() ? 0
                            : Double.parseDouble(etHeight.getText().toString());
                    String sex = spinnerSex.getSelectedItem().toString();

                    dbHelper.saveUser(name, email, goal, address, age, eps, phone, familyPhone, weightStr, heightStr,
                            sex);
                    Toast.makeText(RegisterActivity.this, "¡Bienvenido a GymTrack!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String errorMsg = "Error en el servidor";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            Logger.e("Error registro API: " + response.code() + " - " + errorJson, null);
                            errorMsg = "Error: " + response.code() + " - "
                                    + (errorJson.contains("email") ? "El correo ya existe" : errorJson);
                        }
                    } catch (Exception e) {
                        Logger.e("Error parseando errorBody", e);
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Logger.e("Falla red registro", t);
                Toast.makeText(RegisterActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
