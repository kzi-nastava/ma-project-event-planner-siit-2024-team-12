package com.example.eventplanner.adapters.budget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.SolutionBudgetViewHolder;
import com.example.eventplanner.dto.budget.GetPurchaseAndReservationForBudgetDTO;

import java.util.List;

public class SolutionsAdapter extends RecyclerView.Adapter<SolutionBudgetViewHolder> {

    private final List<GetPurchaseAndReservationForBudgetDTO> solutions;
    private OnSolutionClickListener clickListener;
    public interface OnSolutionClickListener {
        void onSolutionClick(String type, Long solutionId);
    }
    public void setOnSolutionClickListener(OnSolutionClickListener listener) {
        this.clickListener = listener;
    }

    public SolutionsAdapter(List<GetPurchaseAndReservationForBudgetDTO> solutions) {
        this.solutions = solutions;
    }

    @NonNull
    @Override
    public SolutionBudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.solution_item_card, parent, false);
        return new SolutionBudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionBudgetViewHolder holder, int position) {
        GetPurchaseAndReservationForBudgetDTO solution = solutions.get(position);
        holder.bind(solution);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null && solution.getSolution() != null) {
                String type = solution.getSolution().getType();
                Long id = solution.getSolution().getId();
                clickListener.onSolutionClick(type, id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return solutions != null ? solutions.size() : 0;
    }
}