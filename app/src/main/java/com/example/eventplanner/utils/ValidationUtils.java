package com.example.eventplanner.utils;

import android.widget.EditText;

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
            field.setError(errorMessage);
            field.requestFocus();
            return false;
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




    public static boolean isNumberValid(EditText field, String invalidNumberMessage, String negativeNumberMessage) {
        String value = field.getText().toString().trim();
        try {
            int number = Integer.parseInt(value);
            if (number <= 0) {
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
            field.setError("Incorrect format!");
            field.requestFocus();
            return false;
        }

        // check if date is valid ( e.g. there is no February 30 )
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);

            long inputDateMillis = dateFormat.parse(date).getTime();
            long currentDateMillis = System.currentTimeMillis();

            // check if entered date is in the past
            if (inputDateMillis < currentDateMillis) {
                field.setError("Date cannot be in the past!");
                field.requestFocus();
                return false;
            }
        } catch (ParseException e) {
            field.setError("Invalid date!");
            field.requestFocus();
            return false;
        }

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

}
