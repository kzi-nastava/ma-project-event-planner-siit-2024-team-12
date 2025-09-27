package com.example.eventplanner.activities.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.fragments.event.EventDetailsFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.favorites.FavoriteEventsAdapter;
import com.example.eventplanner.dto.event.FavEventDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExplorePageFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavoriteEventsAdapter adapter;
    private List<FavEventDTO> allEvents = new ArrayList<>();
    private List<FavEventDTO> currentEvents = new ArrayList<>();
    private static final int PAGE_SIZE = 3;
    private int currentPage = 1;
    private View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite_events, container, false);

        TextView title = view.findViewById(R.id.title);
        String explore = getString(R.string.explore_public_events);
        title.setText(explore);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadAllEvents();

        adapter = new FavoriteEventsAdapter(currentEvents, eventId -> {
            EventDetailsFragment detailsFragment = EventDetailsFragment.newInstance(eventId);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        loadPage(currentPage);

        view.findViewById(R.id.previousPage).setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadPage(currentPage);
            }
        });

        view.findViewById(R.id.nextPage).setOnClickListener(v -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                loadPage(currentPage);
            }
        });

        updatePageUI();

        return view;
    }





    private void updatePageUI() {
        TextView pageNumberText = view.findViewById(R.id.pageNumber);
        pageNumberText.setText("Page " + currentPage + " / " + getTotalPages());
    }


    private void loadAllEvents() {
        String auth = ClientUtils.getAuthorization(requireContext());

        final List<FavEventDTO>[] openEvents = new List[]{new ArrayList<>()};

        Call<ArrayList<FavEventDTO>> call = ClientUtils.eventService.getOpenEvents(auth);

        call.enqueue(new Callback<ArrayList<FavEventDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<FavEventDTO>> call, Response<ArrayList<FavEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    openEvents[0] = response.body();
                    allEvents.clear();
                    allEvents.addAll(openEvents[0]);
                    loadPage(currentPage);
                } else {
                    Toast.makeText(requireActivity(), "Error loading favorites: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FavEventDTO>> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed to load favorite events!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadPage(int page) {
        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allEvents.size());

        currentEvents.clear();
        currentEvents.addAll(allEvents.subList(startIndex, endIndex));
        adapter.notifyDataSetChanged();

        updatePageUI();
    }


    private int getTotalPages() {
        return (int) Math.ceil((double) allEvents.size() / PAGE_SIZE);
    }

}
