package com.example.gymtrackmovil.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtrackmovil.R;
import com.example.gymtrackmovil.models.Routine;
import java.util.List;

public class RoutinesAdapter extends RecyclerView.Adapter<RoutinesAdapter.ViewHolder> {

    private List<Routine> routineList;

    // Días de la semana para mostrar en cada tarjeta (usa el índice de posición)
    private static final String[] DAYS = {
        "Hoy", "Mañana", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    };

    public RoutinesAdapter(List<Routine> routineList) {
        this.routineList = routineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Routine routine = routineList.get(position);

        // Nombre de la rutina
        holder.tvName.setText(routine.getName());

        // Badge "HOY" solo para el primer elemento
        if (position == 0) {
            holder.tvTodayBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvTodayBadge.setVisibility(View.GONE);
        }

        // Día de la semana – usa dayLabel de la API si existe, sino calcula por posición
        String dayLabel = routine.getDayLabel();
        if (dayLabel != null && !dayLabel.isEmpty()) {
            holder.tvDay.setText(dayLabel);
        } else {
            holder.tvDay.setText(position < DAYS.length ? DAYS[position] : "");
        }

        // Duración
        int duration = routine.getDurationMinutes();
        holder.tvTime.setText(duration > 0 ? duration + " min" : routine.getDescription());

        // Calorías
        int kcal = routine.getCaloriesBurned();
        holder.tvKcal.setText(kcal > 0 ? kcal + " kcal" : "– kcal");

        // Número de ejercicios
        int exercises = routine.getExerciseCount();
        holder.tvExercises.setText(exercises > 0 ? exercises + " ejercicios" : "–");
    }

    @Override
    public int getItemCount() {
        return routineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTodayBadge, tvDay, tvTime, tvKcal, tvExercises;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName        = itemView.findViewById(R.id.tvRoutineNameItem);
            tvTodayBadge  = itemView.findViewById(R.id.tvTodayBadge);
            tvDay         = itemView.findViewById(R.id.tvRoutineDay);
            tvTime        = itemView.findViewById(R.id.tvRoutineTime);
            tvKcal        = itemView.findViewById(R.id.tvRoutineKcal);
            tvExercises   = itemView.findViewById(R.id.tvRoutineExercises);
        }
    }
}
