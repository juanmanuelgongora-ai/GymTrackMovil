package com.example.gymtrackmovil;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtrackmovil.database.DatabaseHelper;
import com.example.gymtrackmovil.utils.Logger;
import com.example.gymtrackmovil.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
public class TrainerDashboardActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private RecyclerView rvClients;
    private TextView tvWelcome, tvClientCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_dashboard);
        Logger.init(this);
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        tvWelcome     = findViewById(R.id.tvTrainerWelcome);
        tvClientCount = findViewById(R.id.tvClientCount);
        rvClients     = findViewById(R.id.rvTrainerClients);
        rvClients.setLayoutManager(new LinearLayoutManager(this));
        String trainerName = sessionManager.getUserName();
        if (tvWelcome != null) tvWelcome.setText("Hola, " + trainerName + " 👋");
        TextView tvUserInitials = findViewById(R.id.tvTrainerInitials);
        if (tvUserInitials != null && trainerName != null) {
            String initials = trainerName.contains(" ")
                    ? "" + trainerName.charAt(0) + trainerName.split(" ")[1].charAt(0)
                    : trainerName.substring(0, Math.min(2, trainerName.length()));
            tvUserInitials.setText(initials.toUpperCase());
            tvUserInitials.setOnClickListener(v -> showLogoutDialog());
        }
        loadClients();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadClients();
    }
    private void loadClients() {
        String trainerEmail = sessionManager.getUserEmail();
        List<ClientItem> clients = new ArrayList<>();
        Cursor cursor = dbHelper.getClientsByTrainer(trainerEmail);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int nameIdx  = cursor.getColumnIndex(DatabaseHelper.KEY_USER_NAME);
                int emailIdx = cursor.getColumnIndex(DatabaseHelper.KEY_USER_EMAIL);
                String cName  = nameIdx  != -1 ? cursor.getString(nameIdx)  : "—";
                String cEmail = emailIdx != -1 ? cursor.getString(emailIdx) : "—";
                clients.add(new ClientItem(cName, cEmail));
            }
            cursor.close();
        }
        if (tvClientCount != null) {
            tvClientCount.setText("Clientes asignados: " + clients.size());
        }
        if (clients.isEmpty()) {
            TextView tvNoClients = findViewById(R.id.tvNoClients);
            if (tvNoClients != null) tvNoClients.setVisibility(View.VISIBLE);
            rvClients.setVisibility(View.GONE);
        } else {
            TextView tvNoClients = findViewById(R.id.tvNoClients);
            if (tvNoClients != null) tvNoClients.setVisibility(View.GONE);
            rvClients.setVisibility(View.VISIBLE);
            rvClients.setAdapter(new ClientAdapter(clients));
        }
    }
    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Deseas cerrar sesión?")
                .setPositiveButton("Sí", (d, w) -> {
                    sessionManager.logoutUser();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null).show();
    }
    static class ClientItem {
        String name, email;
        ClientItem(String name, String email) { this.name = name; this.email = email; }
    }
    static class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.VH> {
        final List<ClientItem> items;
        ClientAdapter(List<ClientItem> items) { this.items = items; }
        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_member_card, parent, false);
            return new VH(v);
        }
        @Override
        public void onBindViewHolder(VH h, int pos) {
            ClientItem c = items.get(pos);
            h.tvName.setText(c.name);
            h.tvEmail.setText(c.email);
            h.tvPlan.setText("Cliente");
            h.tvStatus.setText("Activo");
            String initials = c.name.length() >= 2
                    ? c.name.substring(0, 2).toUpperCase()
                    : c.name.toUpperCase();
            h.tvAvatar.setText(initials);
        }
        @Override
        public int getItemCount() { return items.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvPlan, tvStatus, tvAvatar;
            VH(View v) {
                super(v);
                tvName   = v.findViewById(R.id.tvMemberName);
                tvEmail  = v.findViewById(R.id.tvMemberEmail);
                tvPlan   = v.findViewById(R.id.tvMemberPlan);
                tvStatus = v.findViewById(R.id.tvMemberStatus);
                tvAvatar = v.findViewById(R.id.tvMemberAvatar);
            }
        }
    }
}


