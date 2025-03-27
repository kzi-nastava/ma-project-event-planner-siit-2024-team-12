package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.ViewHolder> {
    private List<String> items;
    private List<String> selectedItems = new ArrayList<>();

    public MultiSelectAdapter(List<String> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multiselect, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = items.get(position);
        holder.textOption.setText(item);
        holder.checkBox.setChecked(selectedItems.contains(item));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }



    public void setSelectedItems(List<String> selectedItems) {
        this.selectedItems.clear();
        this.selectedItems.addAll(selectedItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textOption;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            textOption = itemView.findViewById(R.id.textOption);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
