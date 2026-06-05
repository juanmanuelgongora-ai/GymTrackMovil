package com.example.gymtrackmovil.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtrackmovil.R;
import java.util.List;
public class AdminMembersAdapter extends RecyclerView.Adapter<AdminMembersAdapter.MemberViewHolder> {
    private List<Member> members;
    public static class Member {
        public String name, email, plan, status;
        public Member(String name, String email, String plan, String status) {
            this.name = name;
            this.email = email;
            this.plan = plan;
            this.status = status;
        }
    }
    public AdminMembersAdapter(List<Member> members) {
        this.members = members;
    }
    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_card, parent, false);
        return new MemberViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = members.get(position);
        holder.tvName.setText(member.name);
        holder.tvEmail.setText(member.email);
        holder.tvPlan.setText(member.plan);
        holder.tvStatus.setText(member.status);
        holder.tvAvatar.setText(member.name.substring(0, Math.min(2, member.name.length())).toUpperCase());
    }
    @Override
    public int getItemCount() {
        return members.size();
    }
    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPlan, tvStatus, tvAvatar;
        MemberViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvEmail = itemView.findViewById(R.id.tvMemberEmail);
            tvPlan = itemView.findViewById(R.id.tvMemberPlan);
            tvStatus = itemView.findViewById(R.id.tvMemberStatus);
            tvAvatar = itemView.findViewById(R.id.tvMemberAvatar);
        }
    }
}


