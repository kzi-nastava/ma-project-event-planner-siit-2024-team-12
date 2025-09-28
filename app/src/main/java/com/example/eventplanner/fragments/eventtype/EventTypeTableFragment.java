package com.example.eventplanner.fragments.eventtype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.enumeration.UserRole;

public class EventTypeTableFragment extends Fragment {

    private View view;
    private Button createEventTypeBtn;
    private String role;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event_type_table, container, false);

        TextView title = view.findViewById(R.id.title);
        String adminTitle = getString(R.string.event_types);
        String providerTitle = getString(R.string.provider_event_types);

        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        role = prefs.getString("userRole", UserRole.ROLE_ADMIN.toString());

        if (role.equals(UserRole.ROLE_PROVIDER.toString())) {
            title.setText(providerTitle);
        }
        else {
            title.setText(adminTitle);
        }

        setUpCreateBtn();

        return view;

    }


    private void setUpCreateBtn() {
        createEventTypeBtn = view.findViewById(R.id.createBtn);

        if (role.equals(UserRole.ROLE_PROVIDER.toString())) {
            createEventTypeBtn.setVisibility(View.GONE);
        }

        createEventTypeBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, new EventTypeCreationFragment())
                    .commit();
        });
    }
}