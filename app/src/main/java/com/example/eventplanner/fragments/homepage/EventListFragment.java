package com.example.eventplanner.fragments.homepage;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button prevPageButton, nextPageButton;
    private int currentPage = 0;
    private final int pageSize = 2;
    private final List<String> eventTitles = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        recyclerView = view.findViewById(R.id.eventRecyclerView);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);

        // dummy data
        for (int i = 1; i <= 30; i++) {
            eventTitles.add("Event " + i);
        }

        setupRecyclerView();

        setupPagination();

        return view;
    }

    private void setupRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new EventAdapter(getCurrentPage()));
    }



    private void setupPagination() {
        int totalPages = (int) Math.ceil((double) eventTitles.size() / pageSize);

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updateRecyclerView(totalPages);
            }
        });

        nextPageButton.setOnClickListener(v -> {
            if ((currentPage + 1) * pageSize < eventTitles.size()) {
                currentPage++;
                updateRecyclerView(totalPages);
            }
        });


        addPageIndicators(totalPages);
    }

    private void updateRecyclerView(int totalPages) {

        ((EventAdapter) recyclerView.getAdapter()).updateData(getCurrentPage());

        addPageIndicators(totalPages);
    }

    private void addPageIndicators(int totalPages) {

        if (getView() == null) {
            return;
        }

        LinearLayout paginationIndicators = getView().findViewById(R.id.paginationIndicators);
        paginationIndicators.removeAllViews();

        int lastPage = totalPages - 1;

        if (currentPage > 0) {
            addCircle(paginationIndicators, 0, false); // prvi
            addEllipsis(paginationIndicators);
        }

        if (currentPage > 0) {
            addCircle(paginationIndicators, currentPage - 1, false);
        }

        addCircle(paginationIndicators, currentPage, true);

        if (currentPage < lastPage) {
            addCircle(paginationIndicators, currentPage + 1, false);
        }

        if (lastPage > currentPage + 1) {
            addEllipsis(paginationIndicators);
            addCircle(paginationIndicators, lastPage, false); // poslednja
        }
    }

    private void addCircle(LinearLayout paginationIndicators, int page, boolean isCurrent) {
        TextView circle = new TextView(getContext());
        circle.setText(String.valueOf(page + 1)); // Stranice su 0-based, pa dodajemo 1
        circle.setTextSize(12);
        circle.setTextColor(isCurrent ? Color.WHITE : Color.GRAY);
        circle.setBackgroundResource(R.drawable.circle_indicator);
        circle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        circle.setLayoutParams(params);

        paginationIndicators.addView(circle);
    }

    private void addEllipsis(LinearLayout paginationIndicators) {
        TextView ellipsis = new TextView(getContext());
        ellipsis.setText("...");
        ellipsis.setTextSize(12);
        ellipsis.setTextColor(Color.GRAY);
        ellipsis.setGravity(Gravity.CENTER);

        paginationIndicators.addView(ellipsis);
    }

    private List<String> getCurrentPage() {
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, eventTitles.size());
        return eventTitles.subList(start, end);
    }
}
