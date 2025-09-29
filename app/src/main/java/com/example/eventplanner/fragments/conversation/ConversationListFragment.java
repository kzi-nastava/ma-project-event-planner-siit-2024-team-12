package com.example.eventplanner.fragments.conversation;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.adapters.conversation.ConversationAdapter;
import com.example.eventplanner.dto.conversation.GetChatMessageDTO;
import com.example.eventplanner.dto.conversation.GetConversationDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConversationListFragment extends Fragment implements ConversationWebSocketService.WebSocketUpdateListener {

    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private View emptyStateLayout;
    private ConversationWebSocketService webSocketService;
    private String loggedInUserEmail;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        loggedInUserEmail = ClientUtils.getCurrentUserEmail(getContext());
        recyclerView = view.findViewById(R.id.conversation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ConversationAdapter(new ArrayList<>(), this::onConversationClicked, loggedInUserEmail);
        recyclerView.setAdapter(adapter);
        ImageButton closeButton = view.findViewById(R.id.btn_close_sidebar);
        emptyStateLayout = view.findViewById(R.id.empty_state_layout);

        closeButton.setOnClickListener(v -> closeSidebar());

        loadConversations();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof HomepageActivity) {
            webSocketService = ((HomepageActivity) getActivity()).getConversationService();
            if (webSocketService != null) {
                webSocketService.addUpdateListener(this);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Avoid memory leak
        if (webSocketService != null) {
            webSocketService.removeUpdateListener(this);
        }
    }

    @Override
    public void onConversationUpdated(GetConversationDTO updatedConversation) {
        if (getActivity() != null && adapter != null) {
            getActivity().runOnUiThread(() -> {
                adapter.updateAndMoveToTop(updatedConversation);
                recyclerView.scrollToPosition(0);
            });
        }
    }

    private void onConversationClicked(GetConversationDTO conversation) {
        if (conversation.getId() != null) {

            String otherUserName = getDisplayName(conversation);
            String otherUserEmail = conversation.getOtherUser().getEmail();

            ConversationFragment chatFragment = ConversationFragment.newInstance(
                    conversation.getId(),
                    otherUserName,
                    otherUserEmail,
                    false
            );

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chat_fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("ConversationFragment", "onResume - Reloading conversations to ensure read status is updated.");
        loadConversations();
    }


    private void loadConversations() {
        Log.d("ConversationFragment", "Attempting to load conversations...");
        String authHeader = ClientUtils.getAuthorization(getContext());
        ClientUtils.conversationService.getConversationsForLoggedInUser(authHeader).enqueue(new Callback<List<GetConversationDTO>>() {
            @Override
            public void onResponse(Call<List<GetConversationDTO>> call, Response<List<GetConversationDTO>> response) {

                if (response.isSuccessful()) {
                    List<GetConversationDTO> conversations = response.body();

                    if (conversations != null && !conversations.isEmpty()) {
                        adapter.setConversations(conversations);
                        toggleEmptyState(false);
                    } else {
                        toggleEmptyState(true);

                    }

                } else {
                    Log.e("ConversationFragment", "RESPONSE ERROR: Code: " + response.code() + ", Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("ConversationFragment", "Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("ConversationFragment", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GetConversationDTO>> call, Throwable t) {
                Log.e("ConversationFragment", "FAILURE (Network/API Call): " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private String getDisplayName(GetConversationDTO conversation) {
        if (conversation.getOtherUser() == null) {
            return "User"; // fallback
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

        return "Unknown User";
    }

    private void closeSidebar() {
        if (requireActivity() instanceof HomepageActivity) {
            ((HomepageActivity) requireActivity()).closeChatSidebar();

        } else {
            Log.e("ConversationListFrag", "Parent Activity is not HomepageActivity. Cannot close sidebar.");
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}
