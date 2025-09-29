package com.example.eventplanner.utils;

import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ValidationUtils {
    private ValidationUtils() {}


    public static boolean isFieldValid(EditText field, String errorMessage) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            showError(field, errorMessage);
            return false;
        } else {
            clearError(field);
        }
        return true;
    }



    public static boolean isPhoneValid(EditText field, String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return true;
        }

        try {
            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(phoneNumber, "");
            if (!phoneUtil.isValidNumber(parsedNumber)) {
                field.setError("Invalid phone number format!");
                field.requestFocus();
                return false;
            }
            return true;
        } catch (NumberParseException e) {
            field.setError("Invalid phone number format!");
            field.requestFocus();
            return false;
        }
    }


    public static boolean isEmailValid(EditText field) {
        String email = field.getText().toString().trim();
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (!email.matches(emailPattern)) {
            field.setError("Invalid email format!");
            field.requestFocus();
            return false;
        }
        return true;
    }



    public static boolean isMatchingPassword(EditText passwordField, EditText confirmationField) {
        String password = passwordField.getText().toString().trim();
        String confirmation = confirmationField.getText().toString().trim();

        if (!password.equals(confirmation)) {
            confirmationField.setError("Passwords do not match!");
            confirmationField.requestFocus();
            return false;
        }
        return true;
    }




    public static boolean isNumberValid(EditText field) {
        String value = field.getText().toString().trim();
        try {
            int number = Integer.parseInt(value);
            if (number < 0) {
                field.setError("Enter a positive number!!");
                field.requestFocus();
                return false;
            }
            else if (number > 10000) {
                field.setError("Enter a number less than 10 000!");
                field.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            field.setError("Enter a number!!");
            field.requestFocus();
            return false;
        }
        return true;
    }


    // allows both, integer and double
    public static boolean isDecimalNumber(EditText field, String invalidNumberMessage, String negativeNumberMessage) {
        String input = field.getText().toString().trim();
        if (input.isEmpty()) {
            return false;
        }
        try {
            double number = Double.parseDouble(input);
            if (number < 0) {
                field.setError(negativeNumberMessage);
                field.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            field.setError(invalidNumberMessage);
            field.requestFocus();
            return false;
        }

        return true;

    }


    public static boolean isDateValid(EditText field) {
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$"; // format yyyy-mm-dd
        String date = field.getText().toString().trim();

        if (!date.matches(datePattern)) {
            showError(field, "Incorrect format!");
            return false;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);

            long inputDateMillis = dateFormat.parse(date).getTime();
            long currentDateMillis = System.currentTimeMillis();

            if (inputDateMillis < currentDateMillis) {
                showError(field, "Date cannot be in the past!");
                return false;
            }
        } catch (ParseException e) {
            showError(field, "Invalid date!");
            return false;
        }

        clearError(field);

        return true;
    }


    public static boolean isActivityTimeValid(EditText timeField) {
        String timePattern = "^([01]\\d|2[0-3]):[0-5]\\d - ([01]\\d|2[0-3]):[0-5]\\d$"; // HH:mm - HH:mm
        String timeInput = timeField.getText().toString().trim();

        if (!timeInput.matches(timePattern)) {
            timeField.setError("Incorrect format!");
            timeField.requestFocus();
            return false;
        }

        // check if start time is before end time
        String[] times = timeInput.split(" - ");
        String startTime = times[0];
        String endTime = times[1];

        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setLenient(false);

            long startMillis = timeFormat.parse(startTime).getTime();
            long endMillis = timeFormat.parse(endTime).getTime();

            if (startMillis >= endMillis) {
                timeField.setError("Start time must be earlier than end time!");
                timeField.requestFocus();
                return false;
            }
        } catch (ParseException e) {
            timeField.setError("Invalid time format!");
            timeField.requestFocus();
            return false;
        }

        return true;
    }


    public static boolean isAddressFormatValid(EditText addressField) {
        String address = addressField.getText().toString();
        String[] parts = address.split(",");
        if (parts.length != 3) {
            addressField.setError("Incorrect address format!");
            addressField.requestFocus();
            return false;
        }
        return true;
    }


    private static void showError(EditText field, String errorMessage) {
        if (field.getParent().getParent() instanceof TextInputLayout) {
            TextInputLayout layout = (TextInputLayout) field.getParent().getParent();
            layout.setErrorEnabled(true);
            layout.setError(errorMessage);

            layout.requestLayout();
            layout.invalidate();
        } else {
            field.setError(errorMessage);
        }
        field.requestFocus();
    }


    public static void clearError(EditText field) {
        if (field.getParent().getParent() instanceof TextInputLayout) {
            TextInputLayout layout = (TextInputLayout) field.getParent().getParent();
            layout.setError(null);
            layout.setErrorEnabled(false);
        } else {
            field.setError(null);
        }
    }

}

