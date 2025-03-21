package com.example.eventplanner.adapters.favorites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.solution.FavSolutionDTO;

import java.util.List;

public class FavoriteProductsAdapter extends RecyclerView.Adapter<FavoriteProductsAdapter.ViewHolder> {
    private List<FavSolutionDTO> products;

    public FavoriteProductsAdapter(List<FavSolutionDTO> products) {
        this.products = products;
    }

    @Override
    public FavoriteProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_product, parent, false);
        return new FavoriteProductsAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(FavoriteProductsAdapter.ViewHolder holder, int position) {
        FavSolutionDTO product = products.get(position);

        holder.productTitle.setText(product.getName());
        holder.productLocation.setText(product.getCity());
        holder.productPrice.setText(product.getPrice().toString());
        holder.productCategory.setText(product.getCategoryName());


        if (position % 2 == 0) {
            holder.productImage.setImageResource(R.drawable.product1);
            holder.container.removeAllViews();
            holder.container.addView(holder.productImage);
            holder.container.addView(holder.textContainer);
        } else {
            holder.productImage.setImageResource(R.drawable.product3);
            holder.container.removeAllViews();
            holder.container.addView(holder.textContainer);
            holder.container.addView(holder.productImage);
        }

        holder.seeMore.setOnClickListener(v -> {

        });
    }




    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, seeMore, productLocation, productPrice, productCategory;
        LinearLayout container, textContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView;
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            seeMore = itemView.findViewById(R.id.seeMore);
            textContainer = itemView.findViewById(R.id.textContainer);
            productLocation = itemView.findViewById(R.id.productLocation);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCategory = itemView.findViewById(R.id.productCategory);
        }
    }
}
