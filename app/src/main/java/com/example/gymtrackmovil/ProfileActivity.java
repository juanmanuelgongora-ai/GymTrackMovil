package com.example.gymtrackmovil;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(this);
        TextView tvName = findViewById(R.id.tvProfileName);
        TextView tvEmail = findViewById(R.id.tvProfileEmail);

        tvName.setText(sessionManager.isLoggedIn() ? "Nombre: " + sessionManager.getUserEmail().split("@")[0]
                : "Usuario Invitado");
        tvEmail.setText(sessionManager.isLoggedIn() ? "Correo: " + sessionManager.getUserEmail() : "");
    }
}
