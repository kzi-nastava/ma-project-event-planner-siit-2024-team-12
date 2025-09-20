package com.example.eventplanner.adapters.notification;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventDetailsActivity;
import com.example.eventplanner.dto.notification.GetNotificationDTO;
import com.example.eventplanner.enumeration.NotificationType;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private final Context context;
    private final List<GetNotificationDTO> notificationList;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public NotificationAdapter(Context context, List<GetNotificationDTO> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
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
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView contentTextView;
        private final TextView dateTextView;
        private final ImageView iconImageView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.notification_content);
            dateTextView = itemView.findViewById(R.id.notification_date);
            iconImageView = itemView.findViewById(R.id.notification_icon);
        }

        public void bind(GetNotificationDTO notification) {
            contentTextView.setText(notification.getContent());
            dateTextView.setText(FORMATTER.format(notification.getCreatedAt()));
            iconImageView.setImageResource(getIconForType(notification.getType()));

            if (isClickable(notification)) {
                itemView.setOnClickListener(v -> handleNotificationClick(v.getContext(), notification));
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

        private static boolean isClickable(GetNotificationDTO notification) {
            return notification.getEntityId() != null && notification.getEntityType() != null;
        }

        private static void handleNotificationClick(Context context, GetNotificationDTO notification) {
            if ("EVENT".equals(notification.getEntityType())) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId", notification.getEntityId());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Unsupported entity type.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
