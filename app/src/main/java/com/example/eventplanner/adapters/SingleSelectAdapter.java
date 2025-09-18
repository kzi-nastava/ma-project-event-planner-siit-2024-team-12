package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import java.util.List;

public class SingleSelectAdapter extends RecyclerView.Adapter<SingleSelectAdapter.ViewHolder> {

    private final List<String> items;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public SingleSelectAdapter(List<String> items) {
        this.items = items;
    }

    public String getSelectedItem() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return items.get(selectedPosition);
        }
        return null;
    }

    public void setSelectedItem(String item) {
        int newPosition = items.indexOf(item);
        if (newPosition != -1) {
            int oldPosition = selectedPosition;
            selectedPosition = newPosition;
            if (oldPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldPosition);
            }
            notifyItemChanged(newPosition);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_select_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView optionName;
        View selectedIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            optionName = itemView.findViewById(R.id.optionName);
            selectedIndicator = itemView.findViewById(R.id.selectedIndicator);
            itemView.setOnClickListener(this);
        }

        void bind(String item) {
            optionName.setText(item);
            boolean isSelected = getAdapterPosition() == selectedPosition;
            selectedIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            optionName.setBackgroundResource(isSelected ? R.drawable.selected_filter_item : R.drawable.unselected_filter_item);
        }

        @Override
        public void onClick(View v) {
            int oldPosition = selectedPosition;
            int newPosition = getAdapterPosition();

            if (oldPosition == newPosition) {
                selectedPosition = RecyclerView.NO_POSITION;
            } else {
                selectedPosition = newPosition;
            }

            if (oldPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldPosition);
            }
            if (newPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(newPosition);
            }
        }
    }
}