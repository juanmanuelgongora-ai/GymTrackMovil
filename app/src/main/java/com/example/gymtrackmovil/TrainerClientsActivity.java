package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.adapters.TrainerClientsAdapter;
import com.example.gymtrackmovil.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TrainerClientsActivity extends AppCompatActivity {

    private TrainerClientsAdapter adapter;
    private List<TrainerClientsAdapter.Client> clientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_clients);

        SessionManager sessionManager = new SessionManager(this);

        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        String name = sessionManager.getUserName();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            tvUserInitials.setText(initials.toUpperCase());
        }

        tvUserInitials.setOnClickListener(v -> showLogoutDialog(sessionManager));

        setupClientList();

        EditText etSearch = findViewById(R.id.etSearchClient);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        TextView tvAddClient = findViewById(R.id.tvAddClient);
        tvAddClient.setOnClickListener(v ->
                Toast.makeText(this, "Agregar nuevo cliente", Toast.LENGTH_SHORT).show());

        setupBottomNav();
    }

    private void setupClientList() {
        clientList = new ArrayList<>();
        clientList.add(new TrainerClientsAdapter.Client("Juan García", "Plan Premium", 85, "Hoy"));
        clientList.add(new TrainerClientsAdapter.Client("María López", "Plan Estándar", 70, "Ayer"));
        clientList.add(new TrainerClientsAdapter.Client("Carlos Ruiz", "Plan Premium", 92, "Hace 2 días"));
        clientList.add(new TrainerClientsAdapter.Client("Ana Martínez", "Plan Básico", 60, "Hace 3 días"));
        clientList.add(new TrainerClientsAdapter.Client("Luis Pérez", "Plan Estándar", 78, "Hoy"));

        LinearLayout llClientList = findViewById(R.id.llClientList);
        llClientList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (TrainerClientsAdapter.Client client : clientList) {
            View cardView = inflater.inflate(R.layout.item_trainer_client_card, llClientList, false);

            String[] parts = client.name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                    : client.name.substring(0, Math.min(2, client.name.length()));

            ((TextView) cardView.findViewById(R.id.tvClientAvatar)).setText(initials.toUpperCase());
            ((TextView) cardView.findViewById(R.id.tvClientName)).setText(client.name);
            ((TextView) cardView.findViewById(R.id.tvClientPlan)).setText(client.plan);
            ((TextView) cardView.findViewById(R.id.tvProgressPercent)).setText(client.progressPercent + "%");
            ((TextView) cardView.findViewById(R.id.tvLastSession)).setText("Última sesión: " + client.lastSession);

            final int progress = client.progressPercent;
            View vProgressFill = cardView.findViewById(R.id.vProgressFill);
            android.widget.FrameLayout flProgressBar = cardView.findViewById(R.id.flProgressBar);
            flProgressBar.post(() -> {
                int trackWidth = flProgressBar.getWidth();
                android.view.ViewGroup.LayoutParams params = vProgressFill.getLayoutParams();
                params.width = (int) (trackWidth * (progress / 100f));
                vProgressFill.setLayoutParams(params);
            });

            llClientList.addView(cardView);
        }

        TextView tvClientCount = findViewById(R.id.tvClientCount);
        tvClientCount.setText(clientList.size() + " clientes activos");
    }

    private void setupBottomNav() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navClientes = findViewById(R.id.navClientes);
        LinearLayout navRutinas = findViewById(R.id.navRutinas);
        LinearLayout navPerfil = findViewById(R.id.navPerfil);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerDashboardActivity.class));
            finish();
        });

        navRutinas.setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerRoutinesActivity.class));
            finish();
        });

        navPerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerProfileActivity.class));
            finish();
        });
    }

    private void showLogoutDialog(SessionManager session) {
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
