package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<String> messages;

    public ChatAdapter(List<String> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the chat_item.xml layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Setting the message text in the TextView
        holder.messageTextView.setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        // Define the TextView and ImageView used in the chat_item layout
        TextView messageTextView;
        ImageView profileImageView;
        TextView timeTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views by finding their IDs from the layout
            messageTextView = itemView.findViewById(R.id.message_content);
            profileImageView = itemView.findViewById(R.id.profile_image);
            timeTextView = itemView.findViewById(R.id.message_time);
        }
    }
}
