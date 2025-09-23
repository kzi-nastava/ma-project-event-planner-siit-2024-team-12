package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.budget.BudgetItemAdapter;
import com.example.eventplanner.dto.budget.GetBudgetItemDTO;

import java.util.List;

public class BudgetItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView itemNameTextView;
    private final TextView itemDescriptionTextView;
    private final TextView itemCategoryTextView;
    private final BudgetItemAdapter.OnItemClickListener listener;
    private final List<GetBudgetItemDTO> budgetItems;


    public BudgetItemViewHolder(@NonNull View itemView, BudgetItemAdapter.OnItemClickListener listener, final List<GetBudgetItemDTO> budgetItems) {
        super(itemView);
        itemNameTextView = itemView.findViewById(R.id.tv_item_name);
        itemDescriptionTextView = itemView.findViewById(R.id.tv_item_description);
        itemCategoryTextView = itemView.findViewById(R.id.tv_item_category_display);
        this.listener = listener;
        this.budgetItems = budgetItems;

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < budgetItems.size()) {
                    listener.onItemClick(budgetItems.get(position), position);
                }
            }
        });
    }

    public void bind(GetBudgetItemDTO item) {
        itemNameTextView.setText(item.getName());
        if (item.getCost() != null) {
            itemDescriptionTextView.setText(String.format("Trošak: %.2f", item.getCost()));
        } else {
            itemDescriptionTextView.setText("Trošak: Nije definisan");
        }
        if (item.getCategory() != null) {
            itemCategoryTextView.setText(item.getCategory().getName());
        } else {
            itemCategoryTextView.setText("Kategorija: Nije definisana");
        }

        itemCategoryTextView.setVisibility(View.VISIBLE);
    }
}