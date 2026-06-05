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
    private EditText etName, etLastName, etAddress, etAge, etEmail, etPassword, etPhone, etFamilyPhone;
    private Spinner spinnerEPS;
    private EditText etWeight, etHeight;
    private Spinner spinnerSex;
    private Spinner spinnerGoal;
    private CheckBox cbDataTreatment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Logger.init(this);
        dbHelper = new com.example.gymtrackmovil.database.DatabaseHelper(this);
        viewFlipper = findViewById(R.id.viewFlipper);
        scrollView = findViewById(R.id.scrollView);
        btnBack = findViewById(R.id.btnBack);
        btnContinue = findViewById(R.id.btnContinue);
        tvStep1Circle = findViewById(R.id.tvStep1Circle);
        tvStep2Circle = findViewById(R.id.tvStep2Circle);
        tvStep3Circle = findViewById(R.id.tvStep3Circle);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etAddress = findViewById(R.id.etAddress);
        etAge = findViewById(R.id.etAge);
        etEmail = findViewById(R.id.etEmail);
        spinnerEPS = findViewById(R.id.spinnerEPS);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etFamilyPhone = findViewById(R.id.etFamilyPhone);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        spinnerSex = findViewById(R.id.spinnerSex);
        spinnerGoal = findViewById(R.id.spinnerGoal);
        cbDataTreatment = findViewById(R.id.cbDataTreatment);
        setupSpinners();
        cbDataTreatment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentStep == 2) {
                updateRegisterButtonState(isChecked);
            }
        });
        btnContinue.setOnClickListener(v -> handleContinue());
        btnBack.setOnClickListener(v -> handleBack());
        findViewById(R.id.tvGoToLogin).setOnClickListener(v -> finish());
        updateStepperUI();
    }
    private void setupSpinners() {
        spinnerEPS.setAdapter(buildSpinnerAdapter(new String[]{
                "Seleccione su EPS", "Sura", "Sanitas", "Compensar", "COOMEVA", "Nueva EPS", "Famisanar", "Otro"
        }));
        spinnerSex.setAdapter(buildSpinnerAdapter(new String[]{
                "Seleccione sexo", "Masculino", "Femenino", "Otro"
        }));
        spinnerGoal.setAdapter(buildSpinnerAdapter(new String[]{
                "Seleccione objetivo", "Perder peso", "Ganar masa muscular", "Mantenerse en forma", "Competencia"
        }));
    }
    private ArrayAdapter<String> buildSpinnerAdapter(String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                android.widget.TextView tv = view.findViewById(android.R.id.text1);
                if (tv == null && view instanceof android.widget.TextView) tv = (android.widget.TextView) view;
                if (tv != null) {
                    tv.setTextColor(android.graphics.Color.WHITE);
                    tv.setPadding(32, 0, 32, 0);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
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
        tvStep1Circle.setBackgroundResource(R.drawable.step_circle_inactive);
        tvStep2Circle.setBackgroundResource(R.drawable.step_circle_inactive);
        tvStep3Circle.setBackgroundResource(R.drawable.step_circle_inactive);
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
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String fullName = name + " " + lastName;
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String eps = spinnerEPS.getSelectedItem().toString();
        String goal = spinnerGoal.getSelectedItem().toString();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String familyPhone = etFamilyPhone.getText().toString().trim();
        if (dbHelper.checkUserExists(email)) {
            Toast.makeText(this, "El correo electrónico ya está registrado.", Toast.LENGTH_LONG).show();
            return;
        }
        int age = etAge.getText().toString().isEmpty() ? 0 : Integer.parseInt(etAge.getText().toString());
        double weightStr = etWeight.getText().toString().isEmpty() ? 0 : Double.parseDouble(etWeight.getText().toString());
        double heightStr = etHeight.getText().toString().isEmpty() ? 0 : Double.parseDouble(etHeight.getText().toString());
        String sex = spinnerSex.getSelectedItem().toString();
        long id = dbHelper.saveUser(fullName, email, password, goal, address, age, eps, phone, familyPhone, weightStr, heightStr, sex);
        if (id != -1) {
            Logger.i("Usuario registrado localmente: " + email);
            com.example.gymtrackmovil.utils.SessionManager sessionManager = new com.example.gymtrackmovil.utils.SessionManager(this);
            sessionManager.createLoginSession(email, fullName, "local-session-token-" + email, "cliente");
            Toast.makeText(RegisterActivity.this, "¡Bienvenido a GymTrack! Registro local exitoso.", Toast.LENGTH_LONG).show();
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al guardar el usuario en la base de datos local.", Toast.LENGTH_SHORT).show();
        }
    }
    }

