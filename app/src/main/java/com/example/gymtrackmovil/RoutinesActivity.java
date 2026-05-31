package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtrackmovil.adapters.RoutinesAdapter;
import com.example.gymtrackmovil.api.ApiClient;
import com.example.gymtrackmovil.api.ApiService;
import com.example.gymtrackmovil.models.Routine;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutinesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routines);

        Logger.init(this);
        sessionManager = new SessionManager(this);

        // Avatar initials from email
        TextView tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        String email = sessionManager.getUserEmail();
        if (email != null && !email.isEmpty()) {
            String initial = email.substring(0, 1).toUpperCase();
            tvAvatarInitials.setText(initial);
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.rvRoutines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // API call
        apiService = ApiClient.getClient(this).create(ApiService.class);
        fetchRoutines();

        // Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
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

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private void fetchRoutines() {
        String token = sessionManager.getUserToken();
        Logger.i("Fetch routines token present: " + (token != null));
        // apiService is already initialized in onCreate
        apiService.getRoutines().enqueue(new Callback<List<Routine>>() {
            @Override
            public void onResponse(Call<List<Routine>> call, Response<List<Routine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Routine> routines = response.body();
                    RoutinesAdapter adapter = new RoutinesAdapter(routines);
                    recyclerView.setAdapter(adapter);
                } else {
                    String errorMsg = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Logger.e("Error cargando rutinas: " + errorMsg, null);
                    Toast.makeText(RoutinesActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Routine>> call, Throwable t) {
                Logger.e("Error fetching routines", t);
                Toast.makeText(RoutinesActivity.this,
                        "Falla conexion: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
