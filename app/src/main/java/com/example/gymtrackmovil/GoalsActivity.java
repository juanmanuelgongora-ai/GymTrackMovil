package com.example.gymtrackmovil;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtrackmovil.api.ApiClient;
import com.example.gymtrackmovil.api.ApiService;
import com.example.gymtrackmovil.models.Goal;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class GoalsActivity extends AppCompatActivity {
    private SessionManager session;
    private TextView tvUserInitials;
    private android.widget.LinearLayout llGoalsList;
    private com.example.gymtrackmovil.database.DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        session = new SessionManager(this);
        dbHelper = new com.example.gymtrackmovil.database.DatabaseHelper(this);
        tvUserInitials = findViewById(R.id.tvUserInitials);
        llGoalsList = findViewById(R.id.llGoalsList);
        setupUI();
        fetchGoals();
        findViewById(R.id.btnCreateGoal).setOnClickListener(v -> showAddGoalDialog());
    }
    private void setupUI() {
        String name = session.getUserName();
        if (name.contains(" ")) {
            String[] parts = name.split(" ");
            String initials = "" + parts[0].charAt(0) + parts[1].charAt(0);
            tvUserInitials.setText(initials.toUpperCase());
        } else {
            tvUserInitials.setText(name.substring(0, Math.min(name.length(), 2)).toUpperCase());
        }
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
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
    private void fetchGoals() {
        String email = session.getUserEmail();
        android.database.Cursor check = dbHelper.getUserGoals(email);
        boolean hasGoals = (check != null && check.getCount() > 0);
        if (check != null) check.close();
        if (!hasGoals) {
            dbHelper.saveGoal(email, "Perder Peso Corporal", "Bajar a 80kg", 60, "2026-12-31");
            dbHelper.saveGoal(email, "Ganar Masa Muscular", "Aumentar 5kg de músculo", 40, "2026-12-31");
            dbHelper.saveGoal(email, "Resistencia Cardiovascular", "Correr 10k sin parar", 80, "2026-09-30");
        }
        List<Goal> goalsList = new java.util.ArrayList<>();
        android.database.Cursor cursor = dbHelper.getUserGoals(email);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int tIdx  = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_GOAL_TITLE);
                int taIdx = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_GOAL_TARGET);
                int pIdx  = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_GOAL_PROGRESS);
                int dIdx  = cursor.getColumnIndex(com.example.gymtrackmovil.database.DatabaseHelper.KEY_GOAL_DEADLINE);
                goalsList.add(new Goal(
                        tIdx  != -1 ? cursor.getString(tIdx)  : "",
                        taIdx != -1 ? cursor.getString(taIdx) : "",
                        pIdx  != -1 ? cursor.getInt(pIdx)     : 0,
                        dIdx  != -1 ? cursor.getString(dIdx)  : ""
                ));
            }
            cursor.close();
        }
        updateGoalsUI(goalsList);
    }
    private void updateGoalsUI(List<Goal> goalsList) {
        if (llGoalsList == null) return;
        llGoalsList.removeAllViews();
        int totalProgressSum = 0;
        for (int i = 0; i < goalsList.size(); i++) {
            Goal goal = goalsList.get(i);
            totalProgressSum += goal.getProgress();
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_goal_progress, llGoalsList, false);
            TextView tvGoalName = itemView.findViewById(R.id.tvGoalName);
            TextView tvGoalProgressPercent = itemView.findViewById(R.id.tvGoalProgressPercent);
            ProgressBar pbGoal = itemView.findViewById(R.id.pbGoal);
            tvGoalName.setText(goal.getTitle());
            tvGoalProgressPercent.setText(goal.getProgress() + "%");
            pbGoal.setProgress(goal.getProgress());
            llGoalsList.addView(itemView);
            if (i < goalsList.size() - 1) {
                View spacer = new View(this);
                spacer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(1, (int) (12 * getResources().getDisplayMetrics().density)));
                llGoalsList.addView(spacer);
            }
        }
        if (!goalsList.isEmpty()) {
            int averageProgress = totalProgressSum / goalsList.size();
            TextView tvGenPercent = findViewById(R.id.cardGeneralProgress).findViewById(android.R.id.text1); 
            try {
                androidx.cardview.widget.CardView card = findViewById(R.id.cardGeneralProgress);
                View child = card.getChildAt(0);
                if (child instanceof android.widget.LinearLayout) {
                    android.widget.LinearLayout ll = (android.widget.LinearLayout) child;
                    TextView tvProgressPercent = (TextView) ll.getChildAt(1);
                    ProgressBar pbProgressGen = (ProgressBar) ll.getChildAt(2);
                    tvProgressPercent.setText(averageProgress + "%");
                    pbProgressGen.setProgress(averageProgress);
                }
            } catch (Exception e) {
                Logger.e("Error updating general progress card UI", e);
            }
        }
    }
    private void showAddGoalDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nuevo Objetivo");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_goal, null);
        final EditText etTitle = view.findViewById(R.id.etGoalTitle);
        final EditText etTarget = view.findViewById(R.id.etGoalTarget);
        builder.setView(view);
        builder.setPositiveButton("Crear", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String target = etTarget.getText().toString().trim();
            if (!title.isEmpty()) {
                saveGoal(new Goal(title, target, 0, "2026-12-31"));
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    private void saveGoal(Goal goal) {
        String email = session.getUserEmail();
        long id = dbHelper.saveGoal(email, goal.getTitle(), goal.getTarget(), goal.getProgress(), goal.getDeadline());
        if (id != -1) {
            Toast.makeText(GoalsActivity.this, "Objetivo creado localmente", Toast.LENGTH_SHORT).show();
            fetchGoals();
        } else {
            Toast.makeText(GoalsActivity.this, "Error al crear objetivo en SQLite", Toast.LENGTH_SHORT).show();
        }
    }
}

