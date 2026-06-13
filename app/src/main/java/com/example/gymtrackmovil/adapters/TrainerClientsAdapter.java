package com.example.gymtrackmovil.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymtrackmovil.R;

import java.util.ArrayList;
import java.util.List;

public class TrainerClientsAdapter extends RecyclerView.Adapter<TrainerClientsAdapter.ClientViewHolder> {

    public static class Client {
        public String email;
        public String name;
        public String plan;
        public int progressPercent;
        public String lastSession;

        public Client(String email, String name, String plan, int progressPercent, String lastSession) {
            this.email = email;
            this.name = name;
            this.plan = plan;
            this.progressPercent = progressPercent;
            this.lastSession = lastSession;
        }
    }

    private List<Client> clients;
    private List<Client> clientsFull;

    public TrainerClientsAdapter(List<Client> clients) {
        this.clients = new ArrayList<>(clients);
        this.clientsFull = new ArrayList<>(clients);
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trainer_client_card, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = clients.get(position);

        String[] parts = client.name.trim().split("\\s+");
        String initials = parts.length >= 2
                ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                : client.name.substring(0, Math.min(2, client.name.length()));
        holder.tvAvatar.setText(initials.toUpperCase());

        holder.tvName.setText(client.name);
        holder.tvPlan.setText(client.plan);
        holder.tvProgressPercent.setText(client.progressPercent + "%");
        holder.tvLastSession.setText("Última sesión: " + client.lastSession);

        holder.vProgressFill.post(() -> {
            int trackWidth = holder.flProgressBar.getWidth();
            int fillWidth = (int) (trackWidth * (client.progressPercent / 100f));
            ViewGroup.LayoutParams params = holder.vProgressFill.getLayoutParams();
            params.width = fillWidth;
            holder.vProgressFill.setLayoutParams(params);
        });
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public void filter(String query) {
        clients.clear();
        if (query == null || query.trim().isEmpty()) {
            clients.addAll(clientsFull);
        } else {
            String lower = query.toLowerCase().trim();
            for (Client c : clientsFull) {
                if (c.name.toLowerCase().contains(lower) || c.plan.toLowerCase().contains(lower)) {
                    clients.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvPlan, tvProgressPercent, tvLastSession;
        FrameLayout flProgressBar;
        View vProgressFill;

        ClientViewHolder(View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvClientAvatar);
            tvName = itemView.findViewById(R.id.tvClientName);
            tvPlan = itemView.findViewById(R.id.tvClientPlan);
            tvProgressPercent = itemView.findViewById(R.id.tvProgressPercent);
            tvLastSession = itemView.findViewById(R.id.tvLastSession);
            flProgressBar = itemView.findViewById(R.id.flProgressBar);
            vProgressFill = itemView.findViewById(R.id.vProgressFill);
        }
    }
}
