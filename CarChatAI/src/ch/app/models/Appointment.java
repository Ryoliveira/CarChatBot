package ch.app.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Appointment {
	String date;
	String time;
	UUID creatorId;
	CarInfo carDetail;

	/**
	 * @param appDate    Date of appointment
	 * @param appTime    Time of appointment
	 * @param userId     Users ID for appointment storage
	 * @param carDetails Info of vehicle
	 */
	@JsonCreator
	public Appointment(@JsonProperty("date") String appDate, @JsonProperty("time") String appTime,
			@JsonProperty("creatorId") UUID userId, @JsonProperty("carDetail") CarInfo carDetails) {
		this.date = appDate;
		this.time = appTime;
		this.creatorId = userId;
		this.carDetail = carDetails;
	}

	/**
	 * @return date Date of appointment
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date Date of appointment
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return time Time of appointment
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time Time of appointment
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return creatorId UUID of user who created appointment
	 */
	public UUID getCreatorId() {
		return creatorId;
	}

	/**
	 * @param creator UUID of user who created appointment
	 */
	public void setCreatorId(UUID creator) {
		this.creatorId = creator;
	}

	/**
	 * @return carDetail CarInfo Object
	 */
	public CarInfo getCarDetail() {
		return carDetail;
	}

	/**
	 * @param carDetail CarInfo Object
	 */
	public void setCarDetail(CarInfo carDetail) {
		this.carDetail = carDetail;
	}

	/**
	 * @return formmatedString Car info in formatted string
	 */
	@Override
	public String toString() {
		String[] date = this.getDate().split(":");
		String formattedString = "Date: " + date[1] + "\nTime: " + this.getTime() + "\nCar: "
				+ this.getCarDetail().getMake() + " " + this.getCarDetail().getModel();
		return formattedString;
	}

}
