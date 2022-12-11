package com.example.androidcw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import entities.Expense;
import entities.Trip;

public class AddExpense extends AppCompatActivity {
	Intent intent;
	DBHelper db;

	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_expense);

		intent = getIntent();

		// Init db
		db = new DBHelper(this);

		// Get the tripId send from the previous screen
		// This can never be null
		Trip tripItem = (Trip) intent.getSerializableExtra("tripItem");

		// Get the expenseItem send from the expense list view
		Expense expenseItem = (Expense) intent.getSerializableExtra("expenseItem");
		Expense expense = null;
		if (expenseItem != null) {
			expense = db.getExpense(expenseItem.getTripId(), expenseItem.getId());
		}


		// Get the inputs
		EditText inputExpenseAmount = findViewById(R.id.inputExpenseAmount);
		EditText inputExpenseDate = findViewById(R.id.inputExpenseDate);
		EditText inputExpenseComment = findViewById(R.id.inputExpenseComment);
		Spinner expenseDropdown = findViewById(R.id.expenseSpinner);

		if (expense != null) {
			inputExpenseAmount.setText(expense.getAmount());
			inputExpenseDate.setText(expense.getDate());
			inputExpenseComment.setText(expense.getComments());
		}

		// Initialize the dropdown
		String[] items = new String[]{"Food", "Drinks", "Transportation", "Other"};
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
		expenseDropdown.setAdapter(adapter);

		if (expense != null) {
			int position = Arrays.asList(items).indexOf(expense.getType());
			expenseDropdown.setSelection(position);
		}

		// Show the date modal on clicking the expense date field
		inputExpenseDate.setOnClickListener(view -> {
			AddTrip.CustomDatePicker d = new AddTrip.CustomDatePicker();
			d.setDateField(inputExpenseDate);
			d.show(getSupportFragmentManager(), "Expense Date");
		});

		Button btnBack = findViewById(R.id.btnBack3);
		btnBack.setOnClickListener(view -> this.finish());

		Button btnSave = findViewById(R.id.btnSaveExpense);
		Expense finalExpense = expense;
		btnSave.setOnClickListener(view -> {
			String regex = "^[a-zA-Z0-9-_ ]{3,50}$";
			String numberRegex = "^[\\d]{3,50}$";
			String errorMessage = "The field should contain a-z A-Z 0-9 - _ and from 3 to 20 characters in length";
			String dateRegex = "^(0[1-9]|1[0-9]|2[0-9]|(3[0-1]))[/](0[1-9]|1[0-2])[/]([12][0-9][0-9][0-9])$";

			if (!inputExpenseAmount.getText().toString().matches(numberRegex)) {
				inputExpenseAmount.setError("This field can only contains numbers");
				return;
			} else if (!inputExpenseComment.getText().toString().matches(regex)) {
				inputExpenseComment.setError(errorMessage);
				return;
			} else if (!inputExpenseDate.getText().toString().matches(dateRegex)) {
				inputExpenseDate.setError("Wrong date format");
			}

			// Build the dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to save this expense?");
			builder.setCancelable(true);

			builder.setPositiveButton(
				"Save expense",
				(dialog, id) -> {
					if (finalExpense != null) {
						db.updateExpense(
							finalExpense.getId(),
							expenseDropdown.getSelectedItem().toString(),
							inputExpenseAmount.getText().toString(),
							inputExpenseDate.getText().toString(),
							inputExpenseComment.getText().toString()
						);
					} else {
						db.createExpense(
							tripItem.getId(),
							expenseDropdown.getSelectedItem().toString(),
							inputExpenseAmount.getText().toString(),
							inputExpenseDate.getText().toString(),
							inputExpenseComment.getText().toString()
						);
					}
					Toast.makeText(this, "Expense saved!", Toast.LENGTH_LONG).show();
					this.finish();
				}
			);
			builder.setNegativeButton("Cancel", (dialog, id) -> this.finish());
			builder.create().show();
		});
	}
}