package com.example.gymtrackmovil;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtrackmovil.api.ApiClient;
import com.example.gymtrackmovil.api.ApiService;
import com.example.gymtrackmovil.models.Routine;
import com.example.gymtrackmovil.utils.Logger;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutinesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routines);

        Logger.init(this);
        recyclerView = findViewById(R.id.rvRoutines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);
        fetchRoutines();
    }

    private void fetchRoutines() {
        apiService.getRoutines().enqueue(new Callback<List<Routine>>() {
            @Override
            public void onResponse(Call<List<Routine>> call, Response<List<Routine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.gymtrackmovil.adapters.RoutinesAdapter adapter = new com.example.gymtrackmovil.adapters.RoutinesAdapter(
                            response.body());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Routine>> call, Throwable t) {
                Logger.e("Error fetching routines", t);
                Toast.makeText(RoutinesActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
