package com.example.eventplanner.adapters.conversation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.conversation.GetConversationDTO;
import com.example.eventplanner.dto.conversation.GetChatMessageDTO;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(GetConversationDTO conversation);
    }

    private List<GetConversationDTO> conversations;
    private final OnConversationClickListener listener;

    public ConversationAdapter(List<GetConversationDTO> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    public void setConversations(List<GetConversationDTO> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.bind(conversations.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return conversations != null ? conversations.size() : 0;
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final TextView lastMessage;
        private final TextView lastUpdated;
        private final ImageView profileImage;

        ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.conversation_user_name);
            lastMessage = itemView.findViewById(R.id.conversation_last_message);
            lastUpdated = itemView.findViewById(R.id.conversation_last_updated);
            profileImage = itemView.findViewById(R.id.conversation_profile_image);
        }

        void bind(GetConversationDTO conversation, OnConversationClickListener listener) {
            String fullName = conversation.getOtherUser().getName() + " " + conversation.getOtherUser().getSurname();
            userName.setText(fullName);

            List<GetChatMessageDTO> messages = conversation.getMessages();
            if (messages != null && !messages.isEmpty()) {
                GetChatMessageDTO lastMsg = messages.get(messages.size() - 1);
                lastMessage.setText(lastMsg.getContent());

                if (lastMsg.getTimestamp() != null) {
                    lastUpdated.setText(lastMsg.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM HH:mm")));
                }
            } else {
                lastMessage.setText("No messages yet");
                lastUpdated.setText("");
            }

            itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
        }
    }
}
