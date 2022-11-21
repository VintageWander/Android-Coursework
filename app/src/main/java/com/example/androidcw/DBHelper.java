package com.example.androidcw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import entities.Expense;
import entities.Trip;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TripDB";
    private static final String TABLE_TRIP = "Trip";
    private static final String TABLE_EXPENSE = "Expense";

    // EXPENSE
    private static final String EXPENSE_ID = "expense_id";
//    public static final String TRIP_ID = "trip_id";
    private static final String EXPENSE_TYPE = "expense_type";
    private static final String EXPENSE_AMOUNT = "expense_amount";
    private static final String EXPENSE_DATE = "expense_date";
    private static final String EXPENSE_COMMENT = "expense_comment";

    // TRIP
    public static final String TRIP_ID = "trip_id";
    public static final String TRIP_NAME = "trip_name";
    public static final String TRIP_DATE = "trip_date";
    public static final String TRIP_DESTINATION = "trip_destination";
    public static final String TRIP_RISK_ASSESSMENT = "trip_risk_assessment";
    public static final String TRIP_DESCRIPTION = "trip_description";
    public static final String TRIP_VEHICLE = "trip_vehicle";

    private SQLiteDatabase database;

    private static final String TRIP_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TRIP
                    + " (" + TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TRIP_NAME + " TEXT, "
                    + TRIP_DATE + " TEXT, "
                    + TRIP_DESTINATION + " TEXT, "
                    + TRIP_RISK_ASSESSMENT + " TEXT, "
                    + TRIP_DESCRIPTION + " TEXT, "
                    + TRIP_VEHICLE + " TEXT) ";

    private static final String EXPENSE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_EXPENSE
                    + " (" + EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TRIP_ID + " INTEGER, "
                    + EXPENSE_TYPE + " TEXT, "
                    + EXPENSE_AMOUNT + " TEXT, "
                    + EXPENSE_DATE + " TEXT, "
                    + EXPENSE_COMMENT + " TEXT) ";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 19);
        database = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRIP_TABLE_CREATE);
        db.execSQL(EXPENSE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);

        Log.v(this.getClass().getName(), DATABASE_NAME + " database upgrade to version " +
                newVersion + " - old data lost");
        onCreate(db);
    }

    public long createExpense(int trip_id, String type, String amount, String date, String comment) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(TRIP_ID, trip_id);
        rowValues.put(EXPENSE_TYPE, type);
        rowValues.put(EXPENSE_AMOUNT, amount);
        rowValues.put(EXPENSE_DATE, date);
        rowValues.put(EXPENSE_COMMENT, comment);
        return database.insertOrThrow(TABLE_EXPENSE, null, rowValues);
    }

    public long updateExpense(int id, String type, String amount, String date, String comment) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(EXPENSE_ID, id);
        rowValues.put(EXPENSE_TYPE, type);
        rowValues.put(EXPENSE_AMOUNT, amount);
        rowValues.put(EXPENSE_DATE, date);
        rowValues.put(EXPENSE_COMMENT, comment);
        return database.update(TABLE_EXPENSE, rowValues, EXPENSE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public boolean deleteExpense(int id) {
        // delete
        return database.delete(TABLE_EXPENSE, EXPENSE_ID + "=" + id, null) > 0;
    }


    public long createTrip(String name, String date, String destination, String riskAssessment, String description, String vehicle) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(TRIP_NAME, name);
        rowValues.put(TRIP_DATE, date);
        rowValues.put(TRIP_DESTINATION, destination);
        rowValues.put(TRIP_RISK_ASSESSMENT, riskAssessment);
        rowValues.put(TRIP_DESCRIPTION, description);
        rowValues.put(TRIP_VEHICLE, vehicle);

        return database.insertOrThrow(TABLE_TRIP, null, rowValues);
    }

    public long updateTrip(int id, String name, String date,  String destination, String riskAssessment, String description, String vehicle) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(TRIP_NAME, name);
        rowValues.put(TRIP_DATE, date);
        rowValues.put(TRIP_DESTINATION, destination);
        rowValues.put(TRIP_RISK_ASSESSMENT, riskAssessment);
        rowValues.put(TRIP_DESCRIPTION, description);
        rowValues.put(TRIP_VEHICLE, vehicle);

        return database.update(TABLE_TRIP, rowValues, TRIP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public boolean deleteTrip(int trip_id) {
        // delete
        return database.delete(TABLE_TRIP, TRIP_ID + "=" + trip_id, null) > 0;
    }

    public void clearAll(){
        database.execSQL("delete from " + TABLE_TRIP);
        database.execSQL("delete from " + TABLE_EXPENSE);
    }

    public Expense getExpense(int tripId, int expId) {
        String query = "" +
                "SELECT b.trip_id, " +
                " b.expense_id, " +
                " a.trip_name, " +
                " b.expense_type, " +
                " b.expense_amount, " +
                " b.expense_date, " +
                " b.expense_comment " +
                " FROM " + TABLE_TRIP + " a " +
                " INNER JOIN " + TABLE_EXPENSE + " b " +
                " ON a.trip_id = b.trip_id " +
                " WHERE a.trip_id = ? " +
                " AND b.expense_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(tripId), String.valueOf(expId)});

        int counter = 0;
        cursor.moveToFirst();
        int trip_id = cursor.getInt(counter++);
        int expense_id = cursor.getInt(counter++);
        String trip_name = cursor.getString(counter++);
        String expense_type = cursor.getString(counter++);
        String expense_amount = cursor.getString(counter++);
        String expense_date = cursor.getString(counter++);
        String expense_comment = cursor.getString(counter);

        return new Expense(trip_id, trip_name, expense_id, expense_type, expense_amount, expense_date, expense_comment);
    }

    public List<Expense> getExpenses(int tripId) {

        String MY_QUERY = "" +
                "SELECT b.trip_id, " +
                      " b.expense_id, " +
                      " a.trip_name, " +
                      " b.expense_type, " +
                      " b.expense_amount, " +
                      " b.expense_date, " +
                      " b.expense_comment " +
                " FROM " + TABLE_TRIP + " a " +
                " INNER JOIN " + TABLE_EXPENSE + " b " +
                " ON a.trip_id = b.trip_id " +
                " WHERE a.trip_id = ? ";
        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(tripId)});

        List<Expense> results = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int counter = 0;

            int trip_id = cursor.getInt(counter++);
            int expense_id = cursor.getInt(counter++);
            String trip_name = cursor.getString(counter++);
            String expense_type = cursor.getString(counter++);
            String expense_amount = cursor.getString(counter++);
            String expense_date = cursor.getString(counter++);
            String expense_comment = cursor.getString(counter);

            Expense expense = new Expense(trip_id, trip_name, expense_id, expense_type, expense_amount, expense_date, expense_comment);
            results.add(expense);
            cursor.moveToNext();
        }

        return results;
    }

    public List<Trip> getTrips() {
        Cursor cursor = database.query(
                TABLE_TRIP,
                new String[]{
                        TRIP_ID,
                        TRIP_NAME,
                        TRIP_DATE,
                        TRIP_DESTINATION,
                        TRIP_RISK_ASSESSMENT,
                        TRIP_DESCRIPTION,
                        TRIP_VEHICLE},
                null, null, null, null, TRIP_NAME);

        List<Trip> results = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int counter = 0;
            int id = cursor.getInt(counter++);
            String name = cursor.getString(counter++);
            String date = cursor.getString(counter++);
            String destination = cursor.getString(counter++);
            String risk_assessment = cursor.getString(counter++);
            String description = cursor.getString(counter++);
            String vehicle = cursor.getString(counter);

            Trip trip = new Trip(id, name, date, destination, risk_assessment, description, vehicle);
            results.add(trip);

            cursor.moveToNext();
        }

        return results;
    }

    public Trip getTrip(int id) {
        String query = "SELECT * FROM " + TABLE_TRIP + " WHERE " + TRIP_ID + " = ? ";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});

        cursor.moveToFirst();

        int counter = 0;
        int tripId = cursor.getInt(counter++);
        String name = cursor.getString(counter++);
        String date = cursor.getString(counter++);
        String destination = cursor.getString(counter++);
        String risk_assessment = cursor.getString(counter++);
        String description = cursor.getString(counter++);
        String vehicle = cursor.getString(counter);

        return new Trip(tripId, name, date, destination, risk_assessment, description, vehicle);
    }
}
