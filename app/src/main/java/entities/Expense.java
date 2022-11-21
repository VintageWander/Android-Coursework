package entities;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Expense implements Serializable {
    private int tripId;
    private String tripName;
    // This string field doesn't exists in the expense table in the database
    // It acts like adding extra data into this class's java code

    private int id;
    private String type;
    private String amount;
    private String date;
    private String comments;

    @NonNull
    @Override
    public String toString() {
        return "Expense: " + "\n" +
                "Type: " + type + "\n" +
                "Amount: " + amount + "\n" +
                "Date: " + date + "\n" +
                "Comments: " + comments;
    }

    public Expense(int tripId, String tripName, int id, String type, String amount, String date, String comments) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.comments = comments;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
