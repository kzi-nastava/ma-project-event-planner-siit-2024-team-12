package com.example.eventplanner.adapters.solution;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.solution.GetHomepageSolutionDTO;

import java.util.ArrayList;
import java.util.List;

public class SolutionListAdapter extends RecyclerView.Adapter<SolutionListAdapter.ViewHolder> {

    private List<GetHomepageSolutionDTO> solutions;

    public SolutionListAdapter(List<GetHomepageSolutionDTO> solutions) {
        this.solutions = solutions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Ovde morate da mapirate podatke iz GetHomepageSolutionDTO na UI
        // Za sada, samo prikazujemo ime, a vi dodajte ostale atribute
        GetHomepageSolutionDTO solution = solutions.get(position);
        holder.solutionName.setText(solution.getName());
    }

    @Override
    public int getItemCount() {
        return solutions.size();
    }

    public void updateData(List<GetHomepageSolutionDTO> newSolutions) {
        this.solutions = newSolutions;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView solutionName; // Prilagodite ovo prema vasem layout-u solution_card.xml

        public ViewHolder(View itemView) {
            super(itemView);
            // Inicijalizujte UI elemente iz solution_card.xml
            solutionName = itemView.findViewById(R.id.name); // promenite R.id.solution_name
        }
    }
}
