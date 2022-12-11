package entities;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;

public class Trip implements Serializable {
	private int id;
	private String name;
	private String date;
	private String destination;
	private String riskAssessment;
	private String description;
	private String vehicle;

	public Trip(int id, String name, String date, String destination, String riskAssessment, String description, String vehicle) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.destination = destination;
		this.riskAssessment = riskAssessment;
		this.description = description;
		this.vehicle = vehicle;
	}

	@NonNull
	@Override
	public String toString() {
		return " Trip: " + name + "\n" +
			" Destination: " + destination + "\n" +
			" Started from: " + date + "\n" +
			" With: " + vehicle;
	}

	//    public String toJSON() {
//        return "{" +
//                "   \"name\" : \"" + name + '\"' +
//                "   \"destination\" : \"" + destination + '\"' +
//                "   \"date\" : \"" + date + '\"' +
//                "   \"riskAssessment\" : \"" + riskAssessment + '\"' +
//                "   \"description\" : \"" + description + '\"' +
//                "   \"vehicle\" : \"" + vehicle + '\"' +
//                '}';
//    }
	public String toJson() {
		return new Gson().toJson(this);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRiskAssessment() {
		return riskAssessment;
	}

	public void setRiskAssessment(String riskAssessment) {
		this.riskAssessment = riskAssessment;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}
}
