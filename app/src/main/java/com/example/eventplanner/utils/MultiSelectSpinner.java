package com.example.eventplanner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectSpinner extends androidx.appcompat.widget.AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener {

    private String[] items;
    private boolean[] selected;
    private String defaultText = "Select Event Types";
    private MultiSelectSpinnerListener listener;

    public MultiSelectSpinner(Context context) {
        super(context);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selected != null && which < selected.length) {
            selected[which] = isChecked;
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(defaultText);
        builder.setMultiChoiceItems(items, selected, this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onItemsSelected(selected);
                }
                updateSpinnerSummary();
            }
        });
        builder.show();
        return true;
    }

    private void updateSpinnerSummary() {
        StringBuilder selectedSummary = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < items.length; i++) {
            if (selected[i]) {
                if (!first) {
                    selectedSummary.append(", ");
                }
                selectedSummary.append(items[i]);
                first = false;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{selectedSummary.toString()});
        setAdapter(adapter);
    }

    public void setItems(List<GetEventTypeDTO> eventTypes, List<GetEventTypeDTO> selectedEventTypes) {
        this.items = new String[eventTypes.size()];
        this.selected = new boolean[eventTypes.size()];

        for (int i = 0; i < eventTypes.size(); i++) {
            this.items[i] = eventTypes.get(i).getName();
            // Pre-select items
            for (GetEventTypeDTO selectedType : selectedEventTypes) {
                if (eventTypes.get(i).getId().equals(selectedType.getId())) {
                    this.selected[i] = true;
                    break;
                }
            }
        }
        updateSpinnerSummary();
    }

    public List<GetEventTypeDTO> getSelectedItems(List<GetEventTypeDTO> allEventTypes) {
        List<GetEventTypeDTO> selectedItems = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                selectedItems.add(allEventTypes.get(i));
            }
        }
        return selectedItems;
    }

    public void setMultiSelectSpinnerListener(MultiSelectSpinnerListener listener) {
        this.listener = listener;
    }

    public interface MultiSelectSpinnerListener {
        void onItemsSelected(boolean[] selected);
    }
}