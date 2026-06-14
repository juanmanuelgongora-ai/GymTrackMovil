package com.example.gymtrackmovil;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.adapters.TrainerClientsAdapter;
import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TrainerClientsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String trainerEmail;
    private List<TrainerClientsAdapter.Client> clientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_clients);

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        trainerEmail = sessionManager.getUserEmail();

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

        EditText etSearch = findViewById(R.id.etSearchClient);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderClientList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        TextView tvAddClient = findViewById(R.id.tvAddClient);
        tvAddClient.setOnClickListener(v -> showAddClientDialog());

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText etSearch = findViewById(R.id.etSearchClient);
        renderClientList(etSearch.getText().toString());
    }

    private void renderClientList(String query) {
        if (trainerEmail == null || trainerEmail.isEmpty()) {
            return;
        }

        clientList = new ArrayList<>();

        Cursor cursor = (query == null || query.trim().isEmpty())
                ? dbHelper.getClientsByTrainer(trainerEmail)
                : dbHelper.searchClientsByTrainer(trainerEmail, query.trim());

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_EMAIL));
                String clientName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_NAME));

                int goalIdx = cursor.getColumnIndex(DatabaseHelper.KEY_USER_GOAL);
                String goalText = goalIdx != -1 ? cursor.getString(goalIdx) : null;

                String assignedRoutines = dbHelper.getClientAssignedRoutineNames(email);
                String plan = assignedRoutines != null
                        ? assignedRoutines
                        : (goalText != null && !goalText.trim().isEmpty() ? goalText : "Sin rutina asignada");

                int progress = computeAverageProgress(email);
                String lastSession = computeLastSessionLabel(email);

                clientList.add(new TrainerClientsAdapter.Client(email, clientName, plan, progress, lastSession));
            }
            cursor.close();
        }

        LinearLayout llClientList = findViewById(R.id.llClientList);
        llClientList.removeAllViews();

        if (clientList.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText(query != null && !query.trim().isEmpty()
                    ? "No se encontraron clientes con ese criterio"
                    : "Aún no tienes clientes asignados");
            tvEmpty.setTextColor(0xFFA0A0A0);
            tvEmpty.setTextSize(14);
            tvEmpty.setPadding(0, 24, 0, 24);
            llClientList.addView(tvEmpty);
        } else {
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
                FrameLayout flProgressBar = cardView.findViewById(R.id.flProgressBar);
                flProgressBar.post(() -> {
                    int trackWidth = flProgressBar.getWidth();
                    ViewGroup.LayoutParams params = vProgressFill.getLayoutParams();
                    params.width = (int) (trackWidth * (progress / 100f));
                    vProgressFill.setLayoutParams(params);
                });

                llClientList.addView(cardView);

                cardView.setOnClickListener(v -> showClientOptionsDialog(client));
            }
        }

        int totalClients = dbHelper.countClientsByTrainer(trainerEmail);
        TextView tvClientCount = findViewById(R.id.tvClientCount);
        tvClientCount.setText(totalClients + (totalClients == 1 ? " cliente activo" : " clientes activos"));
    }

    private int computeAverageProgress(String clientEmail) {
        Cursor cursor = dbHelper.getUserGoals(clientEmail);
        int total = 0;
        int count = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idx = cursor.getColumnIndex(DatabaseHelper.KEY_GOAL_PROGRESS);
                if (idx != -1) {
                    total += cursor.getInt(idx);
                    count++;
                }
            }
            cursor.close();
        }
        return count > 0 ? (total / count) : 0;
    }

    private String computeLastSessionLabel(String clientEmail) {
        Cursor cursor = dbHelper.getLastSessionForClient(clientEmail);
        String label = "Sin sesiones";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(DatabaseHelper.KEY_SESSION_DATE);
                if (idx != -1) {
                    String date = cursor.getString(idx);
                    label = date != null && !date.isEmpty() ? date : label;
                }
            }
            cursor.close();
        }
        return label;
    }

    private void showAddClientDialog() {
        Cursor cursor = dbHelper.getUnassignedClients();
        List<String> names = new ArrayList<>();
        List<String> emails = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_EMAIL));
                String clientName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_NAME));
                names.add(clientName + " (" + email + ")");
                emails.add(email);
            }
            cursor.close();
        }

        if (names.isEmpty()) {
            Toast.makeText(this, "No hay clientes disponibles para asignar", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_client, null);
        ListView listView = dialogView.findViewById(R.id.lvUnassignedClients);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_simple_list_white, names);
        listView.setAdapter(adapter);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Seleccionar cliente")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null)
                .create();

        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String selectedEmail = emails.get(position);
            assignClientToTrainer(selectedEmail);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void assignClientToTrainer(String clientEmail) {
        if (dbHelper.clientHasTrainer(clientEmail)) {
            Toast.makeText(this, "Este cliente ya tiene un entrenador asignado", Toast.LENGTH_SHORT).show();
            return;
        }

        int rows = dbHelper.assignClientToTrainer(clientEmail, trainerEmail);
        if (rows > 0) {
            Toast.makeText(this, "Cliente asignado correctamente", Toast.LENGTH_SHORT).show();
            EditText etSearch = findViewById(R.id.etSearchClient);
            renderClientList(etSearch.getText().toString());
        } else {
            Toast.makeText(this, "No se pudo asignar el cliente", Toast.LENGTH_SHORT).show();
        }
    }

    private void showScheduleSessionDialog(TrainerClientsAdapter.Client client) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_schedule_session, null);
        TextView tvScheduleClientName = dialogView.findViewById(R.id.tvScheduleClientName);
        EditText etSessionType = dialogView.findViewById(R.id.etSessionType);
        TextView tvSessionDate = dialogView.findViewById(R.id.tvSessionDate);
        TextView tvSessionTime = dialogView.findViewById(R.id.tvSessionTime);

        tvScheduleClientName.setText("Programar sesión para " + client.name);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        final String[] selectedDate = { null };
        final String[] selectedTime = { null };

        tvSessionDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (picker, year, month, day) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, day);
                selectedDate[0] = dateFormat.format(selected.getTime());
                tvSessionDate.setText(android.text.format.DateFormat.getDateFormat(this).format(selected.getTime()));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        tvSessionTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (picker, hour, minute) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(Calendar.HOUR_OF_DAY, hour);
                selected.set(Calendar.MINUTE, minute);
                selectedTime[0] = timeFormat.format(selected.getTime());
                tvSessionTime.setText(selectedTime[0]);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });

        new android.app.AlertDialog.Builder(this)
                .setTitle("Nueva Sesión")
                .setView(dialogView)
                .setPositiveButton("Programar", (dialog, which) -> {
                    String type = etSessionType.getText().toString().trim();
                    if (type.isEmpty()) {
                        Toast.makeText(this, "Indica el tipo de sesión", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selectedDate[0] == null || selectedTime[0] == null) {
                        Toast.makeText(this, "Selecciona fecha y hora", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbHelper.scheduleTrainingSession(client.email, trainerEmail, type, selectedTime[0], selectedDate[0]);
                    Toast.makeText(this, "Sesión programada correctamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showClientOptionsDialog(TrainerClientsAdapter.Client client) {
        String[] options = { "Programar Sesión", "Asignar Rutina" };

        new android.app.AlertDialog.Builder(this)
                .setTitle(client.name)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showScheduleSessionDialog(client);
                    } else {
                        showAssignRoutineDialog(client);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showAssignRoutineDialog(TrainerClientsAdapter.Client client) {
        Cursor cursor = dbHelper.getRoutinesByTrainer(trainerEmail);

        List<String> names = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long rid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID));
                String rname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ROUTINE_NAME));
                int exIdx = cursor.getColumnIndex(DatabaseHelper.KEY_ROUTINE_EXERCISE_COUNT);
                int durIdx = cursor.getColumnIndex(DatabaseHelper.KEY_ROUTINE_DURATION_MIN);
                int ex = exIdx != -1 ? cursor.getInt(exIdx) : 0;
                int dur = durIdx != -1 ? cursor.getInt(durIdx) : 0;
                names.add(rname + "  (" + ex + " ej. · " + dur + " min)");
                ids.add(rid);
            }
            cursor.close();
        }

        if (names.isEmpty()) {
            Toast.makeText(this, "Aún no has creado rutinas. Créalas en la sección Rutinas.", Toast.LENGTH_LONG).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_assign_routine, null);
        ((TextView) dialogView.findViewById(R.id.tvAssignRoutineClient))
                .setText("Rutina para " + client.name + ":");

        ListView listView = dialogView.findViewById(R.id.lvRoutinesToAssign);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_simple_list_white, names);
        listView.setAdapter(adapter);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Asignar Rutina")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null)
                .create();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            long selectedRoutineId = ids.get(position);
            String selectedName = names.get(position).split("  \\(")[0];
            long result = dbHelper.assignRoutineToClient(client.email, selectedRoutineId);
            if (result > 0) {
                Toast.makeText(this, "Rutina \"" + selectedName + "\" asignada a " + client.name,
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                EditText etSearch = findViewById(R.id.etSearchClient);
                renderClientList(etSearch.getText().toString());
            } else {
                Toast.makeText(this, "Error al asignar la rutina", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
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
