package com.example.eventplanner.adapters.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

    public interface OnImageDeleteListener {
        void onImageDelete(String imageUrl);
    }

    private List<String> imageUrls;
    private OnImageDeleteListener deleteListener;
    private boolean canModify;

    public GalleryAdapter(List<String> imageUrls, boolean canModify, OnImageDeleteListener deleteListener) {
        this.imageUrls = imageUrls;
        this.canModify = canModify;
        this.deleteListener = deleteListener;
    }

    public void setImageUrls(List<String> urls) {
        this.imageUrls = urls;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.user_logo)
                .into(holder.imageView);


        if (canModify) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onImageDelete(imageUrl);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
            holder.deleteButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }


    public void removeImage(String url) {
        imageUrls.remove(url);
        notifyDataSetChanged();
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

}
