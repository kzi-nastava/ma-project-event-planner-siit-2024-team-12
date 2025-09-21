package com.example.eventplanner.adapters.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    public interface OnCategoryClickListener {
        void onCategoryClick(GetCategoryDTO category, boolean isActive);
    }

    private List<GetCategoryDTO> categories = new ArrayList<>();

    public void setCategories(List<GetCategoryDTO> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
    private OnCategoryClickListener listener;
    private boolean isActiveList;

    public CategoryAdapter(boolean isActiveList, OnCategoryClickListener listener) {
        this.isActiveList = isActiveList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        GetCategoryDTO category = categories.get(position);
        holder.bind(category);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category, isActiveList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView descriptionTextView;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewCategoryName);
            descriptionTextView = itemView.findViewById(R.id.textViewCategoryDescription);
        }

        void bind(GetCategoryDTO category) {
            nameTextView.setText(category.getName());
            descriptionTextView.setText(category.getDescription());
        }
    }
}