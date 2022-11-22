package com.example.androidcw;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import entities.Trip;

public class AddTrip extends AppCompatActivity {
    DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        // Instantiate the database - so we can store data in it
        db = new DBHelper(this);
        Intent intent = getIntent();
        Trip tripItem = (Trip) intent.getSerializableExtra("tripItem");
        Trip trip = null;
        if (tripItem != null) {
            trip = db.getTrip(tripItem.getId());
        }

        // Get all the fields
        EditText inputTripName = findViewById(R.id.inputTripName);
        EditText inputDate = findViewById(R.id.inputDate);
        EditText inputDestination = findViewById(R.id.inputDestination);
        SwitchMaterial riskSwitch = findViewById(R.id.riskSwitch);
        Spinner vehicleDropdown = findViewById(R.id.vehicleSpinner);
        EditText inputDescription = findViewById(R.id.inputDescription);

        if (trip != null) {
            inputTripName.setText(trip.getName());
            inputDate.setText(trip.getDate());
            inputDestination.setText(trip.getDestination());
            riskSwitch.setChecked(Objects.equals(trip.getRiskAssessment(), "YES"));
            inputDescription.setText(trip.getDescription());
        }

        // Initialize the dropdown
        String[] items = new String[]{"Car", "Plane", "Train", "Helicopter", "Subway", "UFO", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        vehicleDropdown.setAdapter(adapter);

        if(trip != null) {
            int spinnerPosition = Arrays.asList(items).indexOf(trip.getVehicle());
            vehicleDropdown.setSelection(spinnerPosition);
        }

        // Show the date modal in the input date
        inputDate.setOnClickListener(view -> {
            CustomDatePicker d = new CustomDatePicker();
            d.setDateField(inputDate);
            d.show(getSupportFragmentManager(),"Trip Date");
        });

        // Get toggle status
        boolean isRisk = !riskSwitch.isChecked();

        // Back button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> this.finish());

//        // Show button (for debug)
//        Button btnShow = findViewById(R.id.btnShow);
//        btnShow.setOnClickListener(view -> Toast.makeText(this,
//                vehicleDropdown.getSelectedItem().toString(), Toast.LENGTH_SHORT).show());

        // Save button logic
        Button btnSave = findViewById(R.id.btnSave);
        Trip finalTrip = trip;
        btnSave.setOnClickListener(view -> {
            String regex = "^[a-zA-Z0-9-_ ]{3,50}$";
            String dateRegex = "^(0[1-9]|1[0-9]|2[0-9]|(3[0-1]))[/](0[1-9]|1[0-2])[/]([12][0-9][0-9][0-9])$";
            String errorMessage = "The field should contain a-z A-Z 0-9 - _ and from 3 to 20 characters in length";

            // Validation
            if(!inputTripName.getText().toString().matches(regex) ) {
                inputTripName.setError(errorMessage);
                return;
            } else if(!inputDescription.getText().toString().matches(regex)) {
                inputDescription.setError(errorMessage);
                return;
            } else if (!inputDate.getText().toString().matches(dateRegex)) {
                inputDate.setError("Wrong date");
                return;
            } else if (!inputDestination.getText().toString().matches(regex)) {
                inputDestination.setError(errorMessage);
                return;
            }

            // Build a dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to save this trip?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Save trip",
                    (dialog, id) -> {
                        if (finalTrip != null) {
                            db.updateTrip(
                                    finalTrip.getId(),
                                    inputTripName.getText().toString(),
                                    inputDate.getText().toString(),
                                    inputDestination.getText().toString(),
                                    isRisk ? "YES" : "NO",
                                    inputDescription.getText().toString(),
                                    vehicleDropdown.getSelectedItem().toString()
                            );
                        } else {
                            // Create the trip
                            db.createTrip(
                                    inputTripName.getText().toString(),
                                    inputDate.getText().toString(),
                                    inputDestination.getText().toString(),
                                    isRisk ? "YES" : "NO",
                                    inputDescription.getText().toString(),
                                    vehicleDropdown.getSelectedItem().toString()
                            );
                        }


                        Toast.makeText(this, "Trip saved! ", Toast.LENGTH_LONG).show();
                        // Instead of putting another activity on the activity stack
                        // I call .finish() so I can go back to the main screen
                        this.finish();
                    }
            );

            builder.setNegativeButton(
                    "Cancel",
                    (dialog, id) -> dialog.cancel()
            );
            builder.create().show();
        });
    }

    // datetime picker
    public static class CustomDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public void setDateField(EditText dateField) {
            this.dateField = dateField;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            if (dateField.getText().length() != 0) {
                String date = dateField.getText().toString();
                String[] separated = date.split("/");
                int  year = Integer.parseInt(separated[2]);
                int  month = Integer.parseInt(separated[1]);
                int  day = Integer.parseInt(separated[0]);
                return new DatePickerDialog(getActivity(), this, year, month - 1, day);

            } else {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }
            // Create a new instance of DatePickerDialog and return it
        }

        private EditText dateField;

        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String dateReturn = selectedDay + "/" + (selectedMonth + 1) + "/"
                    + selectedYear;
            dateField.setText(dateReturn);

        }
    }
}