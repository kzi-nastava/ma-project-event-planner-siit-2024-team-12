package com.example.eventplanner.adapters.favorites;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.product.ProductDetailsFragment;
import com.example.eventplanner.dto.product.GetProductDTO;
import com.example.eventplanner.dto.solution.FavSolutionDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.Glide;


public class FavoriteProductsAdapter extends RecyclerView.Adapter<FavoriteProductsAdapter.ViewHolder> {
    public interface OnProductClickListener {
        void onProductClick(Long productId);
    }

    private List<FavSolutionDTO> products;
    private static final String BASE_IMAGE_URL = "http://" + BuildConfig.IP_ADDR + ":8080";
    private final OnProductClickListener listener;


    public FavoriteProductsAdapter(List<FavSolutionDTO> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @Override
    public FavoriteProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_product, parent, false);
        return new FavoriteProductsAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(FavoriteProductsAdapter.ViewHolder holder, int position) {
        FavSolutionDTO product = products.get(position);

        int discountValue = (int) Math.floor(product.getDiscount());
        String discountOff = holder.itemView.getContext().getString(R.string.discount_off, discountValue);

        holder.productTitle.setText(product.getName());
        holder.productLocation.setText(product.getCity());
        holder.productPrice.setText(product.getPrice().toString());
        holder.discount.setText(discountOff);
        holder.productCategory.setText(product.getCategoryName());

        String imageUrl = BASE_IMAGE_URL + product.getMainImageUrl();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.productImage);

        if (position % 2 == 0) {
            holder.container.removeAllViews();
            holder.container.addView(holder.productImage);
            holder.container.addView(holder.textContainer);
        } else {
            holder.container.removeAllViews();
            holder.container.addView(holder.textContainer);
            holder.container.addView(holder.productImage);
        }

        holder.seeMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product.getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return products.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, seeMore, productLocation, productPrice, productCategory, discount;
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
            discount = itemView.findViewById(R.id.discount);
        }
    }
}
