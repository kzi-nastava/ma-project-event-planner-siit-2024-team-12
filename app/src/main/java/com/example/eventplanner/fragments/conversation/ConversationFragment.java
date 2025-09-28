package com.example.eventplanner.fragments.conversation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.conversation.MessageAdapter;
import com.example.eventplanner.dto.conversation.GetChatMessageDTO;
import com.example.eventplanner.dto.conversation.GetConversationDTO;
import com.example.eventplanner.utils.ClientUtils;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationFragment extends Fragment {

    private static final String ARG_CONVERSATION_ID = "conversation_id";
    private static final String ARG_OTHER_USER_NAME = "other_user_name";

    private Long conversationId;
    private String otherUserEmail;
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private TextView otherUserNameTextView;
    private EditText messageInput;
    private ImageButton sendMessageButton;

    public static ConversationFragment newInstance(Long conversationId, String otherUserName, String otherUserEmail) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CONVERSATION_ID, conversationId);
        args.putString(ARG_OTHER_USER_NAME, otherUserName);
        args.putString("other_user_email", otherUserEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            conversationId = getArguments().getLong(ARG_CONVERSATION_ID);
            otherUserEmail = getArguments().getString("other_user_email");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        otherUserNameTextView = view.findViewById(R.id.tv_other_user_name);
        messagesRecyclerView = view.findViewById(R.id.messages_recycler_view);
        messageInput = view.findViewById(R.id.et_message_input);
        sendMessageButton = view.findViewById(R.id.btn_send_message);
        ImageButton backButton = view.findViewById(R.id.btn_back);

        if (getArguments() != null) {
            otherUserNameTextView.setText(getArguments().getString(ARG_OTHER_USER_NAME));
        }

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        messagesRecyclerView.setLayoutManager(layoutManager);

        // Get current user email for adapter
        String currentUserEmail = ClientUtils.getCurrentUserEmail(getContext());
        messageAdapter = new MessageAdapter(Collections.emptyList(), currentUserEmail);
        messagesRecyclerView.setAdapter(messageAdapter);

        loadMessages();

        sendMessageButton.setOnClickListener(v -> sendMessage());

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void loadMessages() {
        String authHeader = ClientUtils.getAuthorization(getContext());
        if (conversationId == null || authHeader.isEmpty()) return;

        ClientUtils.conversationService.markAllAsRead(authHeader, conversationId).enqueue(new Callback<GetConversationDTO>() {
            @Override
            public void onResponse(Call<GetConversationDTO> call, Response<GetConversationDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter.setMessages(response.body().getMessages());
                    messagesRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                } else {
                    Log.e("ConversationFrag", "Failed to load conversation details. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetConversationDTO> call, Throwable t) {
                Log.e("ConversationFrag", "Network error loading messages: " + t.getMessage());
            }
        });
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty() || conversationId == null) return;

        String authHeader = ClientUtils.getAuthorization(getContext());

        ClientUtils.conversationService.sendMessage(authHeader, conversationId, content).enqueue(new Callback<GetChatMessageDTO>() {
            @Override
            public void onResponse(Call<GetChatMessageDTO> call, Response<GetChatMessageDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter.addMessage(response.body());
                    messageInput.setText("");
                    messagesRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                } else {
                    Log.e("ConversationFrag", "Failed to send message. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetChatMessageDTO> call, Throwable t) {
                Log.e("ConversationFrag", "Network error sending message: " + t.getMessage());
            }
        });
    }
    
}
