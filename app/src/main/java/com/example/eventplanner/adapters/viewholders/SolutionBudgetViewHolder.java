package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.budget.GetPurchaseAndReservationForBudgetDTO;
import com.example.eventplanner.utils.ClientUtils;

public class SolutionBudgetViewHolder extends RecyclerView.ViewHolder {

    private final ImageView solutionImageView;
    private final TextView solutionNameTextView;
    private final TextView solutionAmountTextView;
    private final TextView solutionTypeTextView;

    public SolutionBudgetViewHolder(@NonNull View itemView) {
        super(itemView);
        solutionImageView = itemView.findViewById(R.id.iv_solution_image);
        solutionNameTextView = itemView.findViewById(R.id.tv_solution_name);
        solutionAmountTextView = itemView.findViewById(R.id.tv_solution_amount);
        solutionTypeTextView = itemView.findViewById(R.id.tv_solution_type);
    }

    public void bind(GetPurchaseAndReservationForBudgetDTO solutionItem) {
        if (solutionItem != null && solutionItem.getSolution() != null) {
            solutionNameTextView.setText(String.format("Name: "+solutionItem.getSolution().getName()));
            solutionAmountTextView.setText(String.format("Cost: %.2f"+"($)", solutionItem.getAmount()));
            solutionTypeTextView.setText("Type: " + solutionItem.getSolution().getType());

            if (solutionItem.getSolution().getMainImageUrl() != null && !solutionItem.getSolution().getMainImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(ClientUtils.BASE_IMAGE_URL + solutionItem.getSolution().getMainImageUrl())
                        .placeholder(R.drawable.cart)
                        .error(R.drawable.cart)
                        .into(solutionImageView);
            } else {
                solutionImageView.setImageResource(R.drawable.cart);
            }
        }
    }
}
