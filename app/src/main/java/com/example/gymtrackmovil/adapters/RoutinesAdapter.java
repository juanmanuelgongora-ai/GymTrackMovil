package com.example.gymtrackmovil.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtrackmovil.R;
import com.example.gymtrackmovil.models.Routine;
import java.util.List;
public class RoutinesAdapter extends RecyclerView.Adapter<RoutinesAdapter.ViewHolder> {
    private List<Routine> routineList;
    public RoutinesAdapter(List<Routine> routineList) {
        this.routineList = routineList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Routine routine = routineList.get(position);
        holder.tvName.setText(routine.getName());
        holder.tvDescription.setText(routine.getDescription());
    }
    @Override
    public int getItemCount() {
        return routineList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRoutineNameItem);
            tvDescription = itemView.findViewById(R.id.tvRoutineDescItem);
        }
    }
}

