package com.example.gymtrackmovil;
import android.content.Intent;
import android.os.Bundle;
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
    private SessionManager sessionManager; // Declarado para evitar error de compilación
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routines);
        Logger.init(this);
        sessionManager = new SessionManager(this); // Inicializado
        
        recyclerView = findViewById(R.id.rvRoutines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apiService = ApiClient.getClient(this).create(ApiService.class);
        fetchRoutines();
        // Navigation (Solucionado y fusionado con master)
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
        Logger.i("Loading routines locally");
        
        List<Routine> routines = new java.util.ArrayList<>();
        routines.add(new Routine(1, "Rutina de Acondicionamiento General", "Lunes: Pecho y Tríceps. Miércoles: Espalda y Bíceps. Viernes: Pierna Completa."));
        routines.add(new Routine(2, "Rutina Full Body (Cuerpo Completo)", "Tres días a la semana de entrenamiento dinámico multiarticular para hipertrofia y fuerza."));
        routines.add(new Routine(3, "Rutina de Fuerza Máxima", "Entrenamiento enfocado en Powerlifting: Sentadilla, Press de Banca y Peso Muerto con bajas repeticiones."));
        routines.add(new Routine(4, "Rutina de Definición / Cardio", "Ejercicios metabólicos de alta intensidad (HIIT) combinados con pesas de menor carga."));
        
        RoutinesAdapter adapter = new RoutinesAdapter(routines);
        recyclerView.setAdapter(adapter);
    }
}