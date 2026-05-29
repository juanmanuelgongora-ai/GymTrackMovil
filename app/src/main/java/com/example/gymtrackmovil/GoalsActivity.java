package com.example.gymtrackmovil;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.utils.SessionManager;

public class GoalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        SessionManager session = new SessionManager(this);
        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        String name = session.getUserName();
        if (name.contains(" ")) {
            String[] parts = name.split(" ");
            tvUserInitials
                    .setText((String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0))).toUpperCase());
        } else {
            tvUserInitials.setText(name.substring(0, Math.min(name.length(), 2)).toUpperCase());
        }

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

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }
}
