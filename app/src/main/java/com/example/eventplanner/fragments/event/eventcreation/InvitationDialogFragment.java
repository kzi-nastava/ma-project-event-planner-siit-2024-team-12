package com.example.eventplanner.fragments.event.eventcreation;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.CreateEventDTO;
import com.example.eventplanner.viewmodels.EventCreationViewModel;

import java.util.HashSet;
import java.util.Set;

public class InvitationDialogFragment extends DialogFragment {

    private EventCreationViewModel viewModel;
    private EditText emailEditText;
    private EditText invitationContentEditText;
    private ChipGroup invitedEmailsChipGroup;
    private Set<String> invitedEmails = new HashSet<>();
    private TextView invitedEmailsTextView;

    public InvitationDialogFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            getDialog().getWindow().setLayout(width, height);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        return dialog;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitation_dialog, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        emailEditText = view.findViewById(R.id.email_edit_text);

        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addEmail();
                    return true;
                }
                return false;
            }
        });
        ImageButton addEmailButton = view.findViewById(R.id.add_email_button);
        invitedEmailsChipGroup = view.findViewById(R.id.invited_emails_chip_group);
        invitationContentEditText = view.findViewById(R.id.invitation_content_edit_text);
        Button sendButton = view.findViewById(R.id.send_button);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dismiss());

        if (viewModel.getInvitedEmails() != null) {
            invitedEmails.addAll(viewModel.getInvitedEmails());
            updateInvitedEmailsChipGroup();
        }
        if (viewModel.getInvitationContent() != null) {
            invitationContentEditText.setText(viewModel.getInvitationContent());
        }

        addEmailButton.setOnClickListener(v -> addEmail());
        sendButton.setOnClickListener(v -> saveAndClose());

        TextView eventName = view.findViewById(R.id.invitation_event_name);
        TextView eventDescription = view.findViewById(R.id.invitation_event_description);
        TextView eventLocation = view.findViewById(R.id.invitation_event_location);
        TextView eventDate = view.findViewById(R.id.invitation_event_date);

        CreateEventDTO eventDto = viewModel.getDto().getValue();
        if (eventDto != null) {
            eventName.setText(eventDto.getName());
            eventDescription.setText(eventDto.getDescription());
            eventLocation.setText(eventDto.getLocation() != null ? eventDto.getLocation().getAddress() : "Location not set");
            eventDate.setText(eventDto.getDate());
        }

        return view;
    }

    private void addEmail() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (invitedEmails.add(email)) {
            Toast.makeText(getContext(), "Email added!", Toast.LENGTH_SHORT).show();
            updateInvitedEmailsChipGroup();
            emailEditText.setText("");
        } else {
            Toast.makeText(getContext(), "Email already added!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateInvitedEmailsChipGroup() {
        invitedEmailsChipGroup.removeAllViews();

        for (String email : invitedEmails) {
            Chip chip = new Chip(getContext());
            chip.setText(email);
            chip.setCloseIconVisible(true);
            chip.setChipBackgroundColorResource(R.color.light_gray);
            chip.setTextColor(getResources().getColor(R.color.black));

            chip.setOnCloseIconClickListener(v -> {
                invitedEmails.remove(email);
                invitedEmailsChipGroup.removeView(chip);
            });

            invitedEmailsChipGroup.addView(chip);
        }
    }

    private void saveAndClose() {
        if (invitedEmails.isEmpty()) {
            Toast.makeText(getContext(), "Please add at least one email to send invitations.", Toast.LENGTH_SHORT).show();
            return;
        }

        String invitationContent = invitationContentEditText.getText().toString();
        viewModel.setInvitedEmails(invitedEmails);
        viewModel.setInvitationContent(invitationContent);
        Toast.makeText(getContext(), "Invitations details saved!", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}