package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.pricelist.PriceListAdapter;
import com.example.eventplanner.dto.pricelist.GetPriceListItemDTO;
import com.example.eventplanner.dto.pricelist.UpdatePriceListSolutionDTO;

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
    private GetPriceListItemDTO currentItem;

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
                if(priceEditText.getText().toString().trim().isEmpty() || discountEditText.getText().toString().trim().isEmpty()){
                    Toast.makeText(itemView.getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && currentItem != null) {
                    try {
                        double price = Double.parseDouble(priceEditText.getText().toString());
                        double discount = Double.parseDouble(discountEditText.getText().toString());

                        if (price < 0) {
                            Toast.makeText(itemView.getContext(), "Price must be greater than 0.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (discount < 0 || discount > 99) {
                            Toast.makeText(itemView.getContext(), "Discount must be between 0 and 99.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        UpdatePriceListSolutionDTO updateDTO = new UpdatePriceListSolutionDTO(price, discount);

                        listener.onSaveClick(currentItem, updateDTO, position);

                    } catch (NumberFormatException e) {
                        Toast.makeText(itemView.getContext(), "Please enter valid number.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void bind(GetPriceListItemDTO item) {
        this.currentItem = item;
        if (item.getSolution() != null) {
            itemNameTextView.setText(item.getSolution().getName());
            itemDescriptionTextView.setText(item.getSolution().getDescription());
            priceEditText.setText(String.valueOf(item.getSolution().getPrice()));
            discountEditText.setText(String.valueOf(item.getSolution().getDiscount()));

            double originalPrice = item.getSolution().getPrice();
            double discountPercentage = item.getSolution().getDiscount();
            double finalPrice = originalPrice - (originalPrice * (discountPercentage / 100));

            finalPriceTextView.setText(String.format("$%.2f", finalPrice));
            discountInfoTextView.setText(String.format("-%s%%", String.valueOf(discountPercentage)));
        }
    }
}