package com.example.eventplanner.adapters.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.notification.GetNotificationDTO;
import com.example.eventplanner.enumeration.NotificationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    @FunctionalInterface
    public interface NotificationClickListener {
        void onNotificationClick(GetNotificationDTO notification);
    }

    private final Context context;
    private final List<GetNotificationDTO> notificationList;
    private final NotificationClickListener clickListener;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");



    public NotificationAdapter(Context context, List<GetNotificationDTO> notificationList, NotificationClickListener clickListener) {
        this.context = context;
        this.notificationList = notificationList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        GetNotificationDTO notification = notificationList.get(position);
        holder.bind(notification, clickListener);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView contentTextView;
        private final TextView dateTextView;

        private final TextView timeTextView;
        private final ImageView iconImageView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.notification_content);
            dateTextView = itemView.findViewById(R.id.notification_date);
            timeTextView = itemView.findViewById(R.id.notification_time);
            iconImageView = itemView.findViewById(R.id.notification_icon);
        }

        public void bind(GetNotificationDTO notification, NotificationClickListener clickListener) {
            contentTextView.setText(notification.getContent());
            LocalDateTime createdAt = notification.getCreatedAt();
            if (createdAt != null) {
                dateTextView.setText(DATE_FORMATTER.format(createdAt));
                timeTextView.setText(TIME_FORMATTER.format(createdAt));
            } else {
                dateTextView.setText("");
                timeTextView.setText("");
            }
            iconImageView.setImageResource(getIconForType(notification.getType()));

            if (notification.getEntityId() != null && notification.getEntityType() != null) {
                itemView.setOnClickListener(v -> clickListener.onNotificationClick(notification));
            } else {
                itemView.setOnClickListener(null);
            }
        }

        private static int getIconForType(NotificationType type) {
            switch (type) {
                case COMMENT:
                    return R.drawable.ic_comment;
                case RATING:
                    return R.drawable.ic_rating;
                case UPDATE:
                    return R.drawable.ic_update;
                case APPROVAL:
                    return R.drawable.ic_check_circle;
                case REJECTION:
                    return R.drawable.ic_cancel;
                case REMINDER:
                    return R.drawable.ic_reminder;
                default:
                    return R.drawable.ic_notification_generic;
            }
        }
    }
}