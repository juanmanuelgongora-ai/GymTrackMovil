package com.example.gymtrackmovil;

import android.os.Bundle;
import android.content.Intent;
import com.example.gymtrackmovil.R;
import com.example.gymtrackmovil.utils.Logger;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.init(this);
        com.example.gymtrackmovil.utils.SessionManager session = new com.example.gymtrackmovil.utils.SessionManager(
                this);

        findViewById(R.id.navRoutine).setOnClickListener(v -> {
            startActivity(new Intent(this, RoutinesActivity.class));
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Uncomment or add btnLogout in activity_main.xml if needed
        /*
         * findViewById(R.id.btnLogout).setOnClickListener(v -> {
         * session.logoutUser();
         * Intent intent = new Intent(this, LoginActivity.class);
         * startActivity(intent);
         * finish();
         * });
         */
    }
}