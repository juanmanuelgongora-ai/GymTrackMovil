package com.example.gymtrackmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TrainerDashboardActivity extends AppCompatActivity {

    private static final String STATUS_COMPLETADO = "Completado";
    private static final String STATUS_EN_CURSO = "En curso";
    private static final String STATUS_PENDIENTE = "Pendiente";

    static class AgendaSession {
        String clientName;
        String type;
        String time;
        String status;

        AgendaSession(String clientName, String type, String time, String status) {
            this.clientName = clientName;
            this.type = type;
            this.time = time;
            this.status = status;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_dashboard);

        SessionManager sessionManager = new SessionManager(this);

        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        TextView tvSaludo = findViewById(R.id.tvSaludo);

        String name = sessionManager.getUserName();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            tvUserInitials.setText(initials.toUpperCase());

            String firstName = parts[0];
            tvSaludo.setText("¡Hola, " + firstName + "!");
        }

        tvUserInitials.setOnClickListener(v -> showLogoutDialog(sessionManager));

        setupAgenda();
        setupProgressBars();

        findViewById(R.id.btnIniciarSesion).setOnClickListener(v ->
                Toast.makeText(this, "Iniciando sesión con María López", Toast.LENGTH_SHORT).show());

        setupBottomNav();
    }

    private void setupAgenda() {
        List<AgendaSession> sessions = new ArrayList<>();
        sessions.add(new AgendaSession("Juan García", "Fuerza", "09:00", STATUS_COMPLETADO));
        sessions.add(new AgendaSession("María López", "Cardio", "11:00", STATUS_EN_CURSO));
        sessions.add(new AgendaSession("Carlos Ruiz", "HIIT", "15:00", STATUS_PENDIENTE));
        sessions.add(new AgendaSession("Ana Martínez", "Funcional", "17:00", STATUS_PENDIENTE));

        LinearLayout llAgendaList = findViewById(R.id.llAgendaList);
        llAgendaList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (AgendaSession session : sessions) {
            View card = inflater.inflate(R.layout.item_trainer_agenda_session, llAgendaList, false);

            ((TextView) card.findViewById(R.id.tvClientName)).setText(session.clientName);
            ((TextView) card.findViewById(R.id.tvSessionType)).setText(session.type);
            ((TextView) card.findViewById(R.id.tvSessionTime)).setText(session.time);

            TextView tvStatus = card.findViewById(R.id.tvSessionStatus);
            tvStatus.setText(session.status);

            switch (session.status) {
                case STATUS_COMPLETADO:
                    tvStatus.setTextColor(0xFF22C55E);
                    break;
                case STATUS_EN_CURSO:
                    tvStatus.setTextColor(0xFFFF6B35);
                    card.setBackgroundResource(R.drawable.bg_session_card_active);
                    break;
                default:
                    tvStatus.setTextColor(0xFFA0A0A0);
                    break;
            }

            llAgendaList.addView(card);
        }
    }

    private void setupProgressBars() {
        int sesionesCompletadas = 18;
        int sesionesTotales = 20;
        int horasEntrenadas = 24;
        int horasTotales = 30;

        FrameLayout flSesiones = findViewById(R.id.flSesionesProgress);
        View vSesionesFill = findViewById(R.id.vSesionesFill);
        flSesiones.post(() -> {
            int width = flSesiones.getWidth();
            android.view.ViewGroup.LayoutParams params = vSesionesFill.getLayoutParams();
            params.width = (int) (width * ((float) sesionesCompletadas / sesionesTotales));
            vSesionesFill.setLayoutParams(params);
        });

        FrameLayout flHoras = findViewById(R.id.flHorasProgress);
        View vHorasFill = findViewById(R.id.vHorasFill);
        flHoras.post(() -> {
            int width = flHoras.getWidth();
            android.view.ViewGroup.LayoutParams params = vHorasFill.getLayoutParams();
            params.width = (int) (width * ((float) horasEntrenadas / horasTotales));
            vHorasFill.setLayoutParams(params);
        });
    }

    private void setupBottomNav() {
        findViewById(R.id.navClientes).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerClientsActivity.class));
            finish();
        });

        findViewById(R.id.navRutinas).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerRoutinesActivity.class));
            finish();
        });

        findViewById(R.id.navPerfil).setOnClickListener(v -> {
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
