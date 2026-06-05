package com.example.gymtrackmovil.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtrackmovil.R;
import com.example.gymtrackmovil.models.Goal;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalViewHolder> {

    private List<Goal> goals;

    public GoalsAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal_progress, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.tvGoalName.setText(goal.getTitle());
        holder.tvGoalProgressPercent.setText(goal.getProgress() + "%");
        holder.pbGoal.setProgress(goal.getProgress());
    }

    @Override
    public int getItemCount() {
        return goals != null ? goals.size() : 0;
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalName, tvGoalProgressPercent;
        ProgressBar pbGoal;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tvGoalName);
            tvGoalProgressPercent = itemView.findViewById(R.id.tvGoalProgressPercent);
            pbGoal = itemView.findViewById(R.id.pbGoal);
        }
    }
}
