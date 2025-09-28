package com.example.eventplanner.adapters.conversation;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.conversation.GetChatMessageDTO;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private final String currentSenderEmail;
    private List<GetChatMessageDTO> messages;

    public MessageAdapter(List<GetChatMessageDTO> messages, String currentSenderEmail) {
        this.messages = messages;
        this.currentSenderEmail = currentSenderEmail;
    }

    public void setMessages(List<GetChatMessageDTO> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void addMessage(GetChatMessageDTO newMessage) {
        this.messages.add(newMessage);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        GetChatMessageDTO message = messages.get(position);
        if (message.getSenderEmail().equals(currentSenderEmail)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position), getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;
        LinearLayout messageLayout;
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageLayout = (LinearLayout) itemView;
        }

        public void bind(GetChatMessageDTO message, int viewType) {
            messageText.setText(message.getContent());

            if (message.getTimestamp() != null) {
                messageTime.setText(message.getTimestamp().format(TIME_FORMATTER));
            } else {
                messageTime.setText("");
            }

            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                messageText.setBackgroundResource(R.drawable.chat_bubble_right);
                messageText.setTextColor(itemView.getContext().getColor(R.color.white));
                messageLayout.setGravity(Gravity.END);
            } else {
                messageText.setBackgroundResource(R.drawable.chat_bubble_left);
                messageText.setTextColor(itemView.getContext().getColor(R.color.black));
                messageLayout.setGravity(Gravity.START);
            }
        }
    }
}
