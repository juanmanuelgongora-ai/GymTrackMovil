package com.example.gymtrackmovil;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.api.QuoteApiClient;
import com.example.gymtrackmovil.api.QuoteApiService;
import com.example.gymtrackmovil.models.QuoteResponse;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SessionManager session;
    private TextView tvUserInitials, tvWelcomeHeader;
    private TextView tvQuoteText, tvQuoteAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.init(this);
        session = new SessionManager(this);

        tvUserInitials = findViewById(R.id.tvUserInitials);
        tvWelcomeHeader = findViewById(R.id.tvWelcomeHeader);
        tvQuoteText = findViewById(R.id.tvQuoteText);
        tvQuoteAuthor = findViewById(R.id.tvQuoteAuthor);

        updateDynamicUI();
        loadMotivationalQuote();

        findViewById(R.id.navHome).setOnClickListener(v -> {});
        findViewById(R.id.navRoutine).setOnClickListener(v -> startActivity(new Intent(this, RoutinesActivity.class)));
        findViewById(R.id.navProgress).setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        findViewById(R.id.navGoals).setOnClickListener(v -> startActivity(new Intent(this, GoalsActivity.class)));
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        tvUserInitials.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    private void loadMotivationalQuote() {
        QuoteApiService api = QuoteApiClient.getClient().create(QuoteApiService.class);
        api.getRandomQuote().enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QuoteResponse quote = response.body();
                    runOnUiThread(() -> {
                        tvQuoteText.setText("\"" + quote.getQuote() + "\"");
                        tvQuoteAuthor.setText("— " + quote.getAuthor());
                    });
                }
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                Log.e("MainActivity", "Error al obtener frase: " + t.getMessage());
                runOnUiThread(() -> {
                    tvQuoteText.setText("\"El éxito es la suma de pequeños esfuerzos repetidos día tras día.\"");
                    tvQuoteAuthor.setText("— Robert Collier");
                });
            }
        });
    }

    private void updateDynamicUI() {
        String name = session.getUserName();
        tvWelcomeHeader.setText(getString(R.string.welcome_msg, name));
        String initials;
        if (name.contains(" ")) {
            String[] parts = name.split(" ");
            initials = "" + parts[0].charAt(0) + parts[1].charAt(0);
        } else {
            initials = name.substring(0, Math.min(name.length(), 2));
        }
        tvUserInitials.setText(initials.toUpperCase());
    }

    private void showLogoutConfirmDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    session.logoutUser();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
