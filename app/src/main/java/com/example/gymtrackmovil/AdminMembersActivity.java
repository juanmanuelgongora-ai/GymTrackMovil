package com.example.gymtrackmovil;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import com.example.gymtrackmovil.adapters.AdminMembersAdapter;
import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
public class AdminMembersActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private RecyclerView rvMembers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_members);
        Logger.init(this);
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        TextView tvUserInitials = findViewById(R.id.tvUserInitials);
        String name = sessionManager.getUserName();
        if (name != null) {
            String initials;
            if (name.contains(" ")) {
                String[] parts = name.split(" ");
                initials = "" + parts[0].charAt(0) + (parts.length > 1 ? parts[1].charAt(0) : "");
            } else {
                initials = name.substring(0, Math.min(name.length(), 2));
            }
            tvUserInitials.setText(initials.toUpperCase());
        }
        tvUserInitials.setOnClickListener(v -> showLogoutConfirmDialog());
        rvMembers = findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.navAdminHome).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
        });
        android.view.View btnCreateTrainer = findViewById(R.id.fabAddMember);
        if (btnCreateTrainer != null) {
            btnCreateTrainer.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminCreateTrainerActivity.class)));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadUsersFromDB();
    }
    private void loadUsersFromDB() {
        List<AdminMembersAdapter.Member> members = new ArrayList<>();
        Cursor cursor = dbHelper.getAllUsers();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int nameIdx   = cursor.getColumnIndex(DatabaseHelper.KEY_USER_NAME);
                int emailIdx  = cursor.getColumnIndex(DatabaseHelper.KEY_USER_EMAIL);
                int roleIdx   = cursor.getColumnIndex(DatabaseHelper.KEY_USER_ROLE);
                String uName  = nameIdx  != -1 ? cursor.getString(nameIdx)  : "—";
                String uEmail = emailIdx != -1 ? cursor.getString(emailIdx) : "—";
                String uRole  = roleIdx  != -1 ? cursor.getString(roleIdx)  : "cliente";
                String plan   = uRole.equalsIgnoreCase("entrenador") ? "Entrenador" :
                                uRole.equalsIgnoreCase("admin")      ? "Administrador" : "Cliente";
                members.add(new AdminMembersAdapter.Member(uName, uEmail, plan, "Activo"));
            }
            cursor.close();
        }
        if (members.isEmpty()) {
            members.add(new AdminMembersAdapter.Member("Administrador Gym", "admin@gymtrack.com", "Administrador", "Activo"));
        }
        rvMembers.setAdapter(new AdminMembersAdapter(members));
    }
    private void showLogoutConfirmDialog() {
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


