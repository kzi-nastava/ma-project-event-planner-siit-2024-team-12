package com.example.eventplanner.fragments.conversation;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.conversation.ConversationAdapter;
import com.example.eventplanner.dto.conversation.GetConversationDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConversationListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConversationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        recyclerView = view.findViewById(R.id.conversation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ConversationAdapter(new ArrayList<>(), conversation -> {
            // TODO: open messages
        });
        recyclerView.setAdapter(adapter);

        loadConversations();
        return view;
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
                        Log.d("ConversationFragment", "SUCCESS: Conversations loaded. Count: " + conversations.size());
                    } else {
                        Log.d("ConversationFragment", "SUCCESS: No conversations found (server returned 200 OK, but body is null or empty).");
                        // show "no messages yet"
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
}
