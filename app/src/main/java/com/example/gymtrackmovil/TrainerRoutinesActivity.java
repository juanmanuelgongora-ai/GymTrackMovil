package com.example.gymtrackmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.SessionManager;

public class TrainerRoutinesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String trainerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_routines);

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
        tvUserInitials.setOnClickListener(v -> showLogoutDialog());

        findViewById(R.id.tvAddRoutine).setOnClickListener(v -> showCreateRoutineDialog());
        findViewById(R.id.btnCrearRutina).setOnClickListener(v -> showCreateRoutineDialog());

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoutineList();
    }

    private void loadRoutineList() {
        LinearLayout llRoutineList = findViewById(R.id.llRoutineList);
        llRoutineList.removeAllViews();

        Cursor cursor = dbHelper.getRoutinesByTrainer(trainerEmail);

        if (cursor == null || cursor.getCount() == 0) {
            if (cursor != null) cursor.close();
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Aún no has creado rutinas");
            tvEmpty.setTextColor(0xFFA0A0A0);
            tvEmpty.setTextSize(14);
            tvEmpty.setPadding(0, 24, 0, 24);
            llRoutineList.addView(tvEmpty);
            ((TextView) findViewById(R.id.tvRoutineCount)).setText("0 rutinas creadas");
            return;
        }

        int total = cursor.getCount();
        LayoutInflater inflater = LayoutInflater.from(this);

        while (cursor.moveToNext()) {
            long routineId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID));
            String routineName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ROUTINE_NAME));
            String routineDesc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ROUTINE_DESC));

            int exerciseCountIdx = cursor.getColumnIndex(DatabaseHelper.KEY_ROUTINE_EXERCISE_COUNT);
            int durationIdx = cursor.getColumnIndex(DatabaseHelper.KEY_ROUTINE_DURATION_MIN);
            int exerciseCount = exerciseCountIdx != -1 ? cursor.getInt(exerciseCountIdx) : 0;
            int durationMin = durationIdx != -1 ? cursor.getInt(durationIdx) : 0;

            int clientCount = dbHelper.countClientsUsingRoutine(routineId);

            View card = inflater.inflate(R.layout.item_trainer_routine_card, llRoutineList, false);
            ((TextView) card.findViewById(R.id.tvRoutineTitle)).setText(routineName);
            ((TextView) card.findViewById(R.id.tvExerciseCount)).setText(exerciseCount + " ejercicios");
            ((TextView) card.findViewById(R.id.tvDuration)).setText(durationMin + " min");
            ((TextView) card.findViewById(R.id.tvUsedByClients)).setText(
                    "Usada por " + clientCount + (clientCount == 1 ? " cliente" : " clientes"));

            card.findViewById(R.id.tvVerDetalles).setOnClickListener(v ->
                    showRoutineDetailDialog(routineId, routineName, routineDesc, exerciseCount, durationMin));

            card.setOnLongClickListener(v -> {
                confirmDeleteRoutine(routineId, routineName);
                return true;
            });

            llRoutineList.addView(card);
        }
        cursor.close();

        ((TextView) findViewById(R.id.tvRoutineCount)).setText(
                total + (total == 1 ? " rutina creada" : " rutinas creadas"));
    }

    private void showCreateRoutineDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_routine, null);
        EditText etName = dialogView.findViewById(R.id.etRoutineName);
        EditText etDesc = dialogView.findViewById(R.id.etRoutineDesc);
        EditText etExerciseCount = dialogView.findViewById(R.id.etExerciseCount);
        EditText etDurationMin = dialogView.findViewById(R.id.etDurationMin);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Nueva Rutina")
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("El nombre es obligatorio");
                return;
            }

            String desc = etDesc.getText().toString().trim();
            int exerciseCount = 0;
            int durationMin = 0;

            String exStr = etExerciseCount.getText().toString().trim();
            String durStr = etDurationMin.getText().toString().trim();

            if (!exStr.isEmpty()) {
                try { exerciseCount = Integer.parseInt(exStr); } catch (NumberFormatException e) {
                    etExerciseCount.setError("Número inválido");
                    return;
                }
            }
            if (!durStr.isEmpty()) {
                try { durationMin = Integer.parseInt(durStr); } catch (NumberFormatException e) {
                    etDurationMin.setError("Número inválido");
                    return;
                }
            }

            long id = dbHelper.createRoutine(name, desc, trainerEmail, exerciseCount, durationMin);
            if (id > 0) {
                Toast.makeText(this, "Rutina creada correctamente", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadRoutineList();
            } else {
                Toast.makeText(this, "Error al guardar la rutina", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRoutineDetailDialog(long routineId, String name, String desc,
            int exerciseCount, int durationMin) {
        String message = (desc != null && !desc.isEmpty() ? desc + "\n\n" : "") +
                exerciseCount + " ejercicios  •  " + durationMin + " min";

        new android.app.AlertDialog.Builder(this)
                .setTitle(name)
                .setMessage(message.trim())
                .setNeutralButton("Cerrar", null)
                .show();
    }

    private void confirmDeleteRoutine(long routineId, String name) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar rutina")
                .setMessage("¿Deseas eliminar \"" + name + "\"? Se quitará de los clientes que la tengan asignada.")
                .setPositiveButton("Eliminar", (d, w) -> {
                    int rows = dbHelper.deleteRoutine(routineId, trainerEmail);
                    if (rows > 0) {
                        Toast.makeText(this, "Rutina eliminada", Toast.LENGTH_SHORT).show();
                        loadRoutineList();
                    } else {
                        Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerDashboardActivity.class));
            finish();
        });
        findViewById(R.id.navClientes).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerClientsActivity.class));
            finish();
        });
        findViewById(R.id.navPerfil).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainerProfileActivity.class));
            finish();
        });
    }

    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    sessionManager.logoutUser();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
