package com.example.eventplanner.adapters.pricelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.PriceListItemViewHolder;
import com.example.eventplanner.dto.pricelist.GetPriceListItemDTO;
import com.example.eventplanner.dto.pricelist.UpdatePriceListSolutionDTO;

import java.util.List;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListItemViewHolder> {

    private List<GetPriceListItemDTO> priceListItems;
    private OnItemActionListener listener;

    public PriceListAdapter(List<GetPriceListItemDTO> priceListItems) {
        this.priceListItems = priceListItems;
    }

    public interface OnItemActionListener {
        void onSaveClick(GetPriceListItemDTO item, UpdatePriceListSolutionDTO updateDTO, int position);
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PriceListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_list_item_card, parent, false);
        return new PriceListItemViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceListItemViewHolder holder, int position) {
        GetPriceListItemDTO item = priceListItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return priceListItems != null ? priceListItems.size() : 0;
    }

    public void setItems(List<GetPriceListItemDTO> newItems) {
        this.priceListItems = newItems;
        notifyDataSetChanged();
    }
    public List<GetPriceListItemDTO> getItems() {
        return priceListItems;
    }
}