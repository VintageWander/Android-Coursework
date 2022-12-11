package com.example.androidcw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import entities.Expense;
import entities.Trip;

public class TripDetails extends AppCompatActivity {
	DBHelper db;
	Intent intent;
	List<Expense> expenses;
	ListView expenseListView;
	ArrayAdapter<Expense> adapter;

	@Override
	protected void onResume() {
		super.onResume();
		db = new DBHelper(this);

		intent = getIntent();
		Trip tripItem = (Trip) intent.getSerializableExtra("tripItem");
		Trip trip = db.getTrip(tripItem.getId());

		// Get all the values from the trip item and render them
		TextView textTripName = findViewById(R.id.textTripName);
		textTripName.setText(trip.getName());

		TextView textDestination = findViewById(R.id.textDestination);
		textDestination.setText(trip.getDestination());

		TextView textDate = findViewById(R.id.textDate);
		textDate.setText(trip.getDate());

		TextView textRisk = findViewById(R.id.textRisk);
		textRisk.setText(trip.getRiskAssessment());

		TextView textDescription = findViewById(R.id.textDescription);
		textDescription.setText(trip.getDescription());

		TextView textVehicle = findViewById(R.id.textVehicle);
		textVehicle.setText(trip.getVehicle());

		// Render the list of expenses
		expenses = db.getExpenses(tripItem.getId());
		expenseListView = findViewById(R.id.expenseListView);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenses);
		expenseListView.setAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);

		// Get the intent
		intent = getIntent();
		db = new DBHelper(this);

		// Get the data from the intent sent from the previous screen
		Trip tripItem = (Trip) intent.getSerializableExtra("tripItem");
		Trip trip = db.getTrip(tripItem.getId());

		// Get all the values from the trip item and render them
		TextView textTripName = findViewById(R.id.textTripName);
		textTripName.setText(trip.getName());

		TextView textDestination = findViewById(R.id.textDestination);
		textDestination.setText(trip.getDestination());

		TextView textDate = findViewById(R.id.textDate);
		textDate.setText(trip.getDate());

		TextView textRisk = findViewById(R.id.textRisk);
		textRisk.setText(trip.getRiskAssessment());

		TextView textDescription = findViewById(R.id.textDescription);
		textDescription.setText(trip.getDescription());

		TextView textVehicle = findViewById(R.id.textVehicle);
		textVehicle.setText(trip.getVehicle());

		Button btnBack2 = findViewById(R.id.btnBack2);
		btnBack2.setOnClickListener(view -> this.finish());

		Button btnEdit = findViewById(R.id.btnEdit);
		btnEdit.setOnClickListener(view ->
			startActivity(new Intent(this, AddTrip.class).putExtra("tripItem", trip))
		);

		Button btnDelete = findViewById(R.id.btnDeleteTrip);
		btnDelete.setOnClickListener(view -> {
			db.deleteTrip(trip.getId());
			this.finish();
		});

		// Render the list of expenses
		expenses = db.getExpenses(trip.getId());
		expenseListView = findViewById(R.id.expenseListView);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenses);
		expenseListView.setAdapter(adapter);

		expenseListView.setOnItemClickListener((adapterView, view1, id, l) ->
			startActivity(
				new Intent(this, ExpenseDetails.class)
					.putExtra("expenseItem", adapter.getItem(id))
					.putExtra("tripItem", trip)
			));

		Button btnAddExpense = findViewById(R.id.btnAddExpense);
		btnAddExpense.setOnClickListener(view ->
			startActivity(
				new Intent(this, AddExpense.class)
					.putExtra("tripItem", trip)
			)
		);
	}
}