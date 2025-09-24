package com.example.eventplanner.fragments.pricelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.pricelist.PriceListAdapter;
import com.example.eventplanner.dto.pricelist.GetPriceListItemDTO;
import com.example.eventplanner.dto.pricelist.GetPriceListSolutionDTO;
import com.example.eventplanner.viewmodels.PriceListViewModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PriceListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String type;
    private PriceListViewModel viewModel;
    private TextView tvTitle;
    private TextView tvValidFrom;
    private RecyclerView rvPriceListItems;
    private PriceListAdapter adapter;

    public PriceListFragment() {
        // Obavezni prazan konstruktor
    }

    public static PriceListFragment newInstance(String type) {
        PriceListFragment fragment = new PriceListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
        viewModel = new ViewModelProvider(this).get(PriceListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_list, container, false);

        tvTitle = view.findViewById(R.id.tv_title);
        tvValidFrom = view.findViewById(R.id.tv_valid_from);
        rvPriceListItems = view.findViewById(R.id.rv_price_list_items);

        rvPriceListItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PriceListAdapter(new ArrayList<>());
        rvPriceListItems.setAdapter(adapter);

        adapter.setOnItemActionListener((item, updateDTO, position) -> {
            viewModel.updatePriceListItem(item.getId(), type, updateDTO);
        });

        setupObservers();

        if (type != null) {
            viewModel.fetchPriceList(type);
        }

        return view;
    }

    private void setupObservers() {
        viewModel.getPriceList().observe(getViewLifecycleOwner(), priceListDTO -> {
            if (priceListDTO != null) {
                tvValidFrom.setText("Valid from: " + priceListDTO.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
                adapter.setItems(priceListDTO.getPriceListItems());
            } else {
                tvValidFrom.setText("No price list available.");
                adapter.setItems(new ArrayList<>());
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUpdatedItem().observe(getViewLifecycleOwner(), updatedItem -> {
            if (updatedItem != null) {
                List<GetPriceListItemDTO> currentItems = adapter.getItems();
                for (int i = 0; i < currentItems.size(); i++) {
                    if (currentItems.get(i).getId().equals(updatedItem.getId())) {
                        GetPriceListSolutionDTO solution = new GetPriceListSolutionDTO();
                        solution.setPrice(updatedItem.getSolution().getPrice());
                        solution.setDiscount(updatedItem.getSolution().getDiscount());
                        solution.setDescription(currentItems.get(i).getSolution().getDescription());
                        solution.setName(currentItems.get(i).getSolution().getName());
                        currentItems.set(i, new GetPriceListItemDTO(
                                updatedItem.getId(),
                                updatedItem.getDiscountPrice(),
                                solution
                        ));
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
                Toast.makeText(getContext(), "Item updated successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}