package com.example.eventplanner.adapters.budget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.BudgetItemViewHolder;
import com.example.eventplanner.dto.budget.GetBudgetItemDTO;

import java.util.ArrayList;
import java.util.List;

public class BudgetItemAdapter extends RecyclerView.Adapter<BudgetItemViewHolder> {

    private List<GetBudgetItemDTO> budgetItems;
    private OnItemClickListener listener; // Dodajemo listener

    public BudgetItemAdapter(List<GetBudgetItemDTO> budgetItems) {
        this.budgetItems = budgetItems;
    }
    public interface OnItemClickListener {
        void onItemClick(GetBudgetItemDTO item, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_item_card, parent, false);
        return new BudgetItemViewHolder(view, listener, budgetItems);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemViewHolder holder, int position) {
        GetBudgetItemDTO item = budgetItems.get(position);
        holder.bind(item);
    }
    public void updateItem(GetBudgetItemDTO updatedItem, int position) {
        if (position >= 0 && position < budgetItems.size()) {
            budgetItems.set(position, updatedItem);
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return budgetItems != null ? budgetItems.size() : 0;
    }

    public void setItems(List<GetBudgetItemDTO> newItems) {
        this.budgetItems = newItems;
        notifyDataSetChanged();
    }
    // Nova metoda za dodavanje stavke
    public void addItem(GetBudgetItemDTO newItem) {
        if (budgetItems == null) {
            budgetItems = new ArrayList<>();
        }
        budgetItems.add(newItem);
        notifyItemInserted(budgetItems.size() - 1);
    }

    // Getter za listu, potreban za slanje na backend
    public List<GetBudgetItemDTO> getItems() {
        return budgetItems;
    }
}