package com.example.gymtrackmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrainerDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String trainerEmail;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    static class AgendaSession {
        long id;
        String clientName;
        String type;
        String time;
        String status;

        AgendaSession(long id, String clientName, String type, String time, String status) {
            this.id = id;
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

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        trainerEmail = sessionManager.getUserEmail();

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

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        if (trainerEmail == null || trainerEmail.isEmpty()) {
            return;
        }

        String today = dateFormat.format(new Date());

        int totalClients = dbHelper.countClientsByTrainer(trainerEmail);
        int sessionsToday = dbHelper.countSessionsByTrainerAndDate(trainerEmail, today);
        int completedToday = dbHelper.countCompletedSessionsByTrainerAndDate(trainerEmail, today);

        TextView tvSesionesHoy = findViewById(R.id.tvSesionesHoy);
        tvSesionesHoy.setText(sessionsToday > 0
                ? "Tienes " + sessionsToday + " sesiones hoy"
                : "No tienes sesiones programadas hoy");

        TextView tvStatClientes = findViewById(R.id.tvStatClientes);
        tvStatClientes.setText(String.valueOf(totalClients));

        TextView tvStatSesionesHoy = findViewById(R.id.tvStatSesionesHoy);
        tvStatSesionesHoy.setText(String.valueOf(sessionsToday));

        Cursor profileCursor = dbHelper.getTrainerProfile(trainerEmail);
        TextView tvStatRating = findViewById(R.id.tvStatRating);
        if (profileCursor != null) {
            if (profileCursor.moveToFirst()) {
                int ratingIdx = profileCursor.getColumnIndex(DatabaseHelper.KEY_TP_RATING);
                double rating = ratingIdx != -1 ? profileCursor.getDouble(ratingIdx) : 0.0;
                tvStatRating.setText(rating > 0 ? String.format(Locale.getDefault(), "%.1f", rating) : "—");
            } else {
                tvStatRating.setText("—");
            }
            profileCursor.close();
        } else {
            tvStatRating.setText("—");
        }

        TextView tvSesionesCompletadasWeek = findViewById(R.id.tvSesionesCompletadasWeek);
        TextView tvHorasEntrenadasWeek = findViewById(R.id.tvHorasEntrenadasWeek);
        FrameLayout flSesionesProgress = findViewById(R.id.flSesionesProgress);
        View vSesionesFill = findViewById(R.id.vSesionesFill);
        FrameLayout flHorasProgress = findViewById(R.id.flHorasProgress);
        View vHorasFill = findViewById(R.id.vHorasFill);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        String endDate = dateFormat.format(calendar.getTime());
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -6);
        String startDate = dateFormat.format(calendar.getTime());

        int weekTotal = 0;
        int weekCompleted = 0;
        long totalMillis = 0;

        Cursor weekCursor = dbHelper.getSessionsByTrainerBetweenDates(trainerEmail, startDate, endDate);
        if (weekCursor != null) {
            while (weekCursor.moveToNext()) {
                weekTotal++;
                String status = weekCursor.getString(weekCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_STATUS));
                if (DatabaseHelper.STATUS_COMPLETADO.equals(status)) {
                    weekCompleted++;
                    long start = weekCursor.getLong(weekCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_START_TIMESTAMP));
                    long end = weekCursor.getLong(weekCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_END_TIMESTAMP));
                    if (end > start && start > 0) {
                        totalMillis += (end - start);
                    }
                }
            }
            weekCursor.close();
        }

        tvSesionesCompletadasWeek.setText(weekCompleted + "/" + weekTotal);

        double totalHours = totalMillis / 3600000.0;
        int expectedHours = weekTotal; // assume ~1 hour per scheduled session
        tvHorasEntrenadasWeek.setText(String.format(Locale.getDefault(), "%.1f/%dh", totalHours, Math.max(expectedHours, 1)));

        int sesionesPercent = weekTotal > 0 ? (weekCompleted * 100 / weekTotal) : 0;
        int horasPercent = expectedHours > 0 ? (int) Math.min(100, (totalHours / expectedHours) * 100) : 0;

        flSesionesProgress.post(() -> setFillWidth(vSesionesFill, flSesionesProgress, sesionesPercent));
        flHorasProgress.post(() -> setFillWidth(vHorasFill, flHorasProgress, horasPercent));

        setupAgenda(today);
        setupNextSession(today);
    }

    private void setFillWidth(View fill, FrameLayout container, int percent) {
        int width = (int) (container.getWidth() * (percent / 100.0));
        ViewGroup.LayoutParams params = fill.getLayoutParams();
        params.width = Math.max(width, 0);
        fill.setLayoutParams(params);
    }

    private void setupAgenda(String today) {
        List<AgendaSession> sessions = new ArrayList<>();

        Cursor cursor = dbHelper.getSessionsByTrainerAndDate(trainerEmail, today);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID));
                String clientEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_CLIENT_EMAIL));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_TYPE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_TIME));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_STATUS));

                String clientName = getClientNameByEmail(clientEmail);
                sessions.add(new AgendaSession(id, clientName, type, time, status));
            }
            cursor.close();
        }

        LinearLayout llAgendaList = findViewById(R.id.llAgendaList);
        llAgendaList.removeAllViews();

        if (sessions.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No hay sesiones programadas para hoy");
            tvEmpty.setTextColor(0xFFA0A0A0);
            tvEmpty.setTextSize(14);
            llAgendaList.addView(tvEmpty);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (AgendaSession session : sessions) {
            View card = inflater.inflate(R.layout.item_trainer_agenda_session, llAgendaList, false);
            View cardContainer = card.findViewById(R.id.llAgendaCard);

            ((TextView) card.findViewById(R.id.tvClientName)).setText(session.clientName);
            ((TextView) card.findViewById(R.id.tvSessionType)).setText(session.type);
            ((TextView) card.findViewById(R.id.tvSessionTime)).setText(session.time);

            TextView tvStatus = card.findViewById(R.id.tvSessionStatus);
            tvStatus.setText(session.status);

            switch (session.status) {
                case DatabaseHelper.STATUS_COMPLETADO:
                    tvStatus.setTextColor(0xFF22C55E);
                    break;
                case DatabaseHelper.STATUS_EN_CURSO:
                    tvStatus.setTextColor(0xFFFF6B35);
                    cardContainer.setBackgroundResource(R.drawable.bg_session_card_active);
                    break;
                default:
                    tvStatus.setTextColor(0xFFA0A0A0);
                    break;
            }

            llAgendaList.addView(card);
        }
    }

    private void setupNextSession(String today) {
        View llNextSessionCard = findViewById(R.id.llNextSessionCard);
        TextView tvNextSessionClient = findViewById(R.id.tvNextSessionClient);
        TextView tvNextSessionTime = findViewById(R.id.tvNextSessionTime);
        TextView tvNextSessionType = findViewById(R.id.tvNextSessionType);
        TextView tvBtnIniciarSesion = findViewById(R.id.tvBtnIniciarSesion);
        View btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        Cursor activeCursor = dbHelper.getActiveSessionForTrainer(trainerEmail);
        if (activeCursor != null && activeCursor.moveToFirst()) {
            long id = activeCursor.getLong(activeCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID));
            String clientEmail = activeCursor.getString(activeCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_CLIENT_EMAIL));
            String type = activeCursor.getString(activeCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_TYPE));
            String time = activeCursor.getString(activeCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_TIME));
            activeCursor.close();

            llNextSessionCard.setVisibility(View.VISIBLE);
            tvNextSessionClient.setText(getClientNameByEmail(clientEmail));
            tvNextSessionTime.setText("🕐 " + time);
            tvNextSessionType.setText("💪 " + type);
            tvBtnIniciarSesion.setText("Finalizar Sesión");

            btnIniciarSesion.setOnClickListener(v -> finishActiveSession(id));
            return;
        }
        if (activeCursor != null) {
            activeCursor.close();
        }

        AgendaSession pending = findNextPendingSession(today);
        if (pending == null) {
            llNextSessionCard.setVisibility(View.GONE);
            return;
        }

        llNextSessionCard.setVisibility(View.VISIBLE);
        tvNextSessionClient.setText(pending.clientName);
        tvNextSessionTime.setText("🕐 " + pending.time);
        tvNextSessionType.setText("💪 " + pending.type);
        tvBtnIniciarSesion.setText("Iniciar Sesión");

        btnIniciarSesion.setOnClickListener(v -> startSession(pending.id, pending.clientName));
    }

    private AgendaSession findNextPendingSession(String today) {
        Cursor cursor = dbHelper.getSessionsByTrainerAndDate(trainerEmail, today);
        AgendaSession result = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_STATUS));
                if (DatabaseHelper.STATUS_PENDIENTE.equals(status)) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID));
                    String clientEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_CLIENT_EMAIL));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_TYPE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SESSION_TIME));
                    result = new AgendaSession(id, getClientNameByEmail(clientEmail), type, time, status);
                    break;
                }
            }
            cursor.close();
        }
        return result;
    }

    private void startSession(long sessionId, String clientName) {
        dbHelper.startTrainingSession(sessionId);
        Toast.makeText(this, "Sesión iniciada con " + clientName, Toast.LENGTH_SHORT).show();
        loadDashboardData();
    }

    private void finishActiveSession(long sessionId) {
        dbHelper.finishTrainingSession(sessionId);
        Toast.makeText(this, "Sesión finalizada", Toast.LENGTH_SHORT).show();
        loadDashboardData();
    }

    private String getClientNameByEmail(String email) {
        if (email == null) return "—";
        Cursor cursor = dbHelper.getUserByEmail(email);
        String name = email;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(DatabaseHelper.KEY_USER_NAME);
                if (idx != -1) {
                    String n = cursor.getString(idx);
                    if (n != null && !n.isEmpty()) {
                        name = n;
                    }
                }
            }
            cursor.close();
        }
        return name;
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
