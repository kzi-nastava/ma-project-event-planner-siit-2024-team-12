package com.example.eventplanner.adapters;



import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.CategoryViewHolder;
import com.example.eventplanner.model.GetSolutionCategoryDTO;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private List<GetSolutionCategoryDTO> categoryList;

    public CategoryAdapter(List<GetSolutionCategoryDTO> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_row, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        GetSolutionCategoryDTO category = categoryList.get(position);

        holder.idTextView.setText(category.getId());
        holder.nameTextView.setText(category.getName());

        if (category.isExpanded()) {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.statusTextView.setVisibility(View.VISIBLE);

            String description = holder.itemView.getContext().getString(R.string.description_in_table_row, category.getDescription());
            // bold "Description : " part
            SpannableString spannable = new SpannableString(description);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.descriptionTextView.setText(spannable);


            String status = holder.itemView.getContext().getString(R.string.status_in_table_row, category.getStatus());
            // bold "Status : " part
            SpannableString spannable2 = new SpannableString(status);
            spannable2.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.statusTextView.setText(spannable2);


            holder.expandArrow.setRotation(180f);
        } else {
            holder.descriptionTextView.setVisibility(View.GONE);
            holder.statusTextView.setVisibility(View.GONE);

            holder.expandArrow.setRotation(0f);
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.even_row_color));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.odd_row_color));
        }


        holder.itemView.setOnClickListener(v -> {
            category.setExpanded(!category.isExpanded());
            notifyItemChanged(position);
        });
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}

