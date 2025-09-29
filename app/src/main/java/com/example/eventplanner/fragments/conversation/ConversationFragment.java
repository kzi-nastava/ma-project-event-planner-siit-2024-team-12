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
import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.adapters.conversation.MessageAdapter;
import com.example.eventplanner.dto.conversation.GetChatMessageDTO;
import com.example.eventplanner.dto.conversation.GetConversationDTO;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationFragment extends Fragment {

    private static final String ARG_CONVERSATION_ID = "conversation_id";
    private static final String ARG_OTHER_USER_NAME = "other_user_name";
    private static final String ARG_IS_NEW_CONVERSATION = "is_new_conversation";


    private boolean isNewConversation;
    private Long conversationId;
    private String otherUserEmail;
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private TextView otherUserNameTextView;
    private EditText messageInput;
    private ImageButton sendMessageButton;
    private View headerLayout;
    private TextView blockMessageTextView;
    private Long otherUserId;
    private Long currentUserId;

    public static ConversationFragment newInstance(Long conversationId, String otherUserName, String otherUserEmail, boolean isNewConversation  ) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CONVERSATION_ID, conversationId);
        args.putString(ARG_OTHER_USER_NAME, otherUserName);
        args.putString("other_user_email", otherUserEmail);
        args.putBoolean(ARG_IS_NEW_CONVERSATION, isNewConversation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            conversationId = getArguments().getLong(ARG_CONVERSATION_ID);
            otherUserEmail = getArguments().getString("other_user_email");
            isNewConversation = getArguments().getBoolean(ARG_IS_NEW_CONVERSATION, false);
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
        headerLayout = view.findViewById(R.id.header_layout);
        blockMessageTextView = view.findViewById(R.id.tv_block_message);

        if (getArguments() != null) {
            String nameFromArgs = getArguments().getString(ARG_OTHER_USER_NAME);
            if (nameFromArgs != null && (nameFromArgs.trim().isEmpty() || nameFromArgs.equals("null null"))) {
                otherUserNameTextView.setText(otherUserEmail);
            } else {
                otherUserNameTextView.setText(nameFromArgs);
            }
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

        backButton.setOnClickListener(v -> {
            if (!isNewConversation) {
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                closeSidebar();
            }
        });

        headerLayout.setOnClickListener(v -> navigateToUserProfile());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof HomepageActivity) {
            ConversationWebSocketService service =
                    ((HomepageActivity) getActivity()).getConversationService();

            if (service != null) {
                service.addConversationListener(this::handleNewNotification);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    if (getActivity() instanceof HomepageActivity) {
        ((HomepageActivity) getActivity()).onConversationOpened();
    }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof HomepageActivity) {
            ((HomepageActivity) getActivity()).onConversationClosed();
        }
    }



    private void handleNewNotification(List<GetChatMessageDTO> newMessages) {
        if (getActivity() == null || newMessages == null || newMessages.isEmpty()) return;

        requireActivity().runOnUiThread(() -> {
            messageAdapter.setMessages(new ArrayList<>());
            for (GetChatMessageDTO msg : newMessages) {
                messageAdapter.addMessage(msg);
                messagesRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
//            markConversationAsReadOnServer();
        });
    }


    private void closeSidebar() {
        if (requireActivity() instanceof HomepageActivity) {
            ((HomepageActivity) requireActivity()).closeChatSidebar();

        } else {
            Log.e("ConversationFrag", "Parent Activity is not HomepageActivity. Cannot close sidebar.");
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
    private void loadMessages() {
        String authHeader = ClientUtils.getAuthorization(getContext());
        if (conversationId == null || authHeader.isEmpty()) return;

        ClientUtils.conversationService.markAllAsRead(authHeader, conversationId).enqueue(new Callback<GetConversationDTO>() {
            @Override
            public void onResponse(Call<GetConversationDTO> call, Response<GetConversationDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetConversationDTO conversation = response.body();

                    String displayName = getDisplayName(conversation);
                    otherUserNameTextView.setText(displayName);

                    if (conversation.isBlocked()) {
                        handleBlock(displayName);
                    } else {
                        messageAdapter.setMessages(conversation.getMessages());
                        messagesRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                        toggleChatElements(false);
                    }
                } else if (response.code() == 403) {
                    handleBlock(otherUserNameTextView.getText().toString());
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

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody requestBody = RequestBody.create(mediaType, content);

        ClientUtils.conversationService.sendMessage(authHeader, conversationId, requestBody).enqueue(new Callback<GetChatMessageDTO>() {
            @Override
            public void onResponse(Call<GetChatMessageDTO> call, Response<GetChatMessageDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter.addMessage(response.body());
                    messageInput.setText("");
                    messagesRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                } else if (response.code() == 403) {
                    handleBlock(otherUserNameTextView.getText().toString());
                }else {
                    Log.e("ConversationFrag", "Failed to send message. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetChatMessageDTO> call, Throwable t) {
                Log.e("ConversationFrag", "Network error sending message: " + t.getMessage());
            }
        });
    }
    private void markConversationAsReadOnServer() {
        if (conversationId == null) return;
        String authHeader = ClientUtils.getAuthorization(getContext());

        ClientUtils.conversationService.markAllAsRead(authHeader, conversationId).enqueue(new Callback<GetConversationDTO>() {
            @Override
            public void onResponse(Call<GetConversationDTO> call, Response<GetConversationDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("ConversationFrag", "Successfully marked conversation " + conversationId + " as read.");
                    // Ovdje ne treba da ažuriramo listu poruka u adapteru,
                    // jer je poruka već prikazana u handleNewNotification
                } else {
                    Log.e("ConversationFrag", "Failed to mark as read: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetConversationDTO> call, Throwable t) {
                Log.e("ConversationFrag", "Network error on markAllAsRead: " + t.getMessage());
            }
        });
    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // UKOLIKO KONVERZACIJA NIJE BLOKIRANA:
//        // Finalni poziv API-ju da osiguramo da je status "pročitano"
//        // ažuriran pre nego što se fragment zatvori.
//        markConversationAsReadOnServer();
//
//        // Obavezno uklonite listener za WebSocket
//        if (getActivity() instanceof HomepageActivity) {
//            ConversationWebSocketService service =
//                    ((HomepageActivity) getActivity()).getConversationService();
//            if (service != null) {
//                service.removeConversationListener(this::handleNewNotification); // MORATE IMPLEMENTIRATI removeConversationListener U WS SERVICE
//            }
//        }
//    }
    private void navigateToUserProfile() {
        if (otherUserEmail == null || getContext() == null) return;
        if (requireActivity() instanceof HomepageActivity) {
            ((HomepageActivity) requireActivity()).openProfileAndCloseChat(otherUserEmail);
        } else {
            Log.e("ConversationFrag", "Parent Activity must be HomepageActivity to handle navigation.");
        }
    }

    private void handleBlock(String blockedUserName) {
        String message = "This conversation is unavailable due to communication restrictions between you and " + blockedUserName + ".";
        blockMessageTextView.setText(message);

        toggleChatElements(true);
    }

    private void toggleChatElements(boolean isBlocked) {
        View inputLayout = getView().findViewById(R.id.message_input_layout);

        if (isBlocked) {
            messagesRecyclerView.setVisibility(View.GONE);
            if (inputLayout != null) {
                inputLayout.setVisibility(View.GONE);
            }
            blockMessageTextView.setVisibility(View.VISIBLE);
        } else {
            messagesRecyclerView.setVisibility(View.VISIBLE);
            if (inputLayout != null) {
                inputLayout.setVisibility(View.VISIBLE);
            }
            blockMessageTextView.setVisibility(View.GONE);
        }
    }

    private String getDisplayName(GetConversationDTO conversation) {
        if (conversation.getOtherUser() == null) {
            return otherUserEmail != null ? otherUserEmail : "User";
        }

        String name = conversation.getOtherUser().getName();
        String surname = conversation.getOtherUser().getSurname();
        String email = conversation.getOtherUser().getEmail();

        if (name != null && !name.trim().isEmpty() && surname != null && !surname.trim().isEmpty()) {
            return name + " " + surname;
        }

        if (email != null && !email.trim().isEmpty()) {
            return email;
        }

        // Fallback
        return otherUserEmail != null ? otherUserEmail : "Unknown User";
    }
    
}
