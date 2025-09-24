package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.pricelist.PriceListAdapter;
import com.example.eventplanner.dto.pricelist.GetPriceListItemDTO;

public class PriceListItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView itemNameTextView;
    private final TextView itemDescriptionTextView;
    private final TextView priceLabelTextView;
    private final EditText priceEditText;
    private final TextView discountLabelTextView;
    private final EditText discountEditText;
    private final TextView finalPriceTextView;
    private final TextView discountInfoTextView;
    private final Button saveButton;
    private final PriceListAdapter.OnItemActionListener listener;

    public PriceListItemViewHolder(@NonNull View itemView, final PriceListAdapter.OnItemActionListener listener) {
        super(itemView);
        itemNameTextView = itemView.findViewById(R.id.tv_item_name);
        itemDescriptionTextView = itemView.findViewById(R.id.tv_item_description);
        priceLabelTextView = itemView.findViewById(R.id.tv_price_label);
        priceEditText = itemView.findViewById(R.id.et_price);
        discountLabelTextView = itemView.findViewById(R.id.tv_discount_label);
        discountEditText = itemView.findViewById(R.id.et_discount);
        finalPriceTextView = itemView.findViewById(R.id.tv_final_price);
        discountInfoTextView = itemView.findViewById(R.id.tv_discount_info);
        saveButton = itemView.findViewById(R.id.btn_save);

        this.listener = listener;

        saveButton.setOnClickListener(v -> {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Potrebno je dobiti referencu na item, adapter će to uraditi
                    // ili ćete proslediti item direktno iz adaptera
                }
            }
        });
    }

    public void bind(GetPriceListItemDTO item) {
        if (item.getSolution() != null) {
            itemNameTextView.setText(item.getSolution().getName());
            itemDescriptionTextView.setText(item.getSolution().getDescription());
            priceEditText.setText(String.valueOf(item.getSolution().getPrice()));
            discountEditText.setText(String.valueOf(item.getSolution().getDiscount()));

            // Prikaz konačne cene i popusta
            double originalPrice = item.getSolution().getPrice();
            double discountPercentage = item.getSolution().getDiscount();
            double finalPrice = originalPrice - (originalPrice * (discountPercentage / 100));

            finalPriceTextView.setText(String.format("$%.2f", finalPrice));
            discountInfoTextView.setText(String.format("-%s%%", String.valueOf(discountPercentage)));
        }
    }
}