package com.example.androidcw;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import entities.Trip;
import io.github.cdimascio.dotenv.Dotenv;


public class MainActivity extends AppCompatActivity {

	static Dotenv dotenv = Dotenv.configure()
		.directory("/assets")
		.filename("env")
		.load();

	private static final String API_URL = dotenv.get("API_URL");

	// Initiate the database
	DBHelper db;
	List<Trip> trips;
	ListView tripListView;
	ArrayAdapter<Trip> adapter;

	@Override
	protected void onResume() {
		super.onResume();
		db = new DBHelper(this);
		trips = db.getTrips();

		// Render the trips inside the list view, using array adapter
		tripListView = findViewById(R.id.tripListView);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trips);
		tripListView.setAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		db = new DBHelper(this);
		trips = db.getTrips();

		// Render the trips inside the list view, using array adapter
		tripListView = findViewById(R.id.tripListView);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trips);
		tripListView.setAdapter(adapter);

		// Logic for searching
		AutoCompleteTextView inputSearch = findViewById(R.id.inputSearch);
		List<String> names = trips.stream().map(Trip::getName).collect(Collectors.toList());
		inputSearch.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
		inputSearch.setThreshold(1);
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				List<Trip> filteredTrips = trips
					.stream()
					.filter(trip -> trip.getName().contains(charSequence))
					.collect(Collectors.toList());
				tripListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, filteredTrips));
			}

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});


		// If the user clicks into one of the list items
		// Send them to the trip details activity
		// Also send the data of that trip to the next activity
		tripListView.setOnItemClickListener((adapterView, view1, id, l) ->
			startActivity(
				new Intent(this, TripDetails.class)
					.putExtra("tripItem", adapter.getItem(id)
					)
			)
		);

		// Clearing the input when click on the clear button
		Button btnClearSearch = findViewById(R.id.btnClearSearch);
		btnClearSearch.setOnClickListener(view -> {
			inputSearch.setText("");
			tripListView.setAdapter(adapter);
		});

		// Clearing all trips, with showing a modal message box before deleting
		Button btnClearTrips = findViewById(R.id.btnClearTrips);
		btnClearTrips.setOnClickListener(view -> {
			// Build the message box
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Inject the message
			builder.setMessage("Are you sure you want to clear all trips?");
			// Enable the "cancel" button
			builder.setCancelable(true);

			// Here I will program 2 buttons, left (negative) and right (positive)
			// Right button
			builder.setPositiveButton(
				"Clear trips",
				(dialog, id) -> {
					db.clearAll();
					Toast.makeText(getApplicationContext(), "All trips has been cleared!", Toast.LENGTH_SHORT).show();
					onResume();
				}
			);

			// Left button
			builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

			// After build, show the dialog
			builder.create().show();
		});

		// Click "+" button to go to the add page
		Button btnAdd = findViewById(R.id.btnAddTrip);
		btnAdd.setOnClickListener(view ->
			startActivity(new Intent(this, AddTrip.class))
		);

		// Cloud upload
		Button btnUpload = findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(view -> {

			// Create a json request and put some properties in it
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("username", "giang");
			jsonObject.add("trips", new Gson().toJsonTree(trips)); // Serialize the trips list into Json
			// Convert it into string
			final String request = jsonObject.toString();

			// Create a request queue, preparing to send data
			RequestQueue requestQueue = Volley.newRequestQueue(this);

			StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, response -> {
				Log.i("Response: ", response);
				Toast.makeText(getApplicationContext(), "Data upload to cloud successfully ", Toast.LENGTH_SHORT).show();
			}, error -> {
				Log.i("Error: ", error.toString());
				Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
			}) {
				@Override
				public String getBodyContentType() {
					return "application/json; charset=utf-8";
				}

				@Override
				public byte[] getBody() {
					return request.getBytes(StandardCharsets.UTF_8);
				}

				@Override
				protected Response<String> parseNetworkResponse(NetworkResponse response) {
					String responseString = "";
					if (response != null) {
						responseString = String.valueOf(response.statusCode);
						// can get more details such as response.headers
					}
					assert response != null;
					return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
				}
			};

			requestQueue.add(stringRequest);
		});
	}
}