package ch.app.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CarInfo{
	
	String make;
	String model;
	String price;
	String leaseLength;
	
	
	/**
	 * @param carMake
	 * @param carModel
	 * @param carPrice
	 * @param carLease
	 */
	@JsonCreator
	public CarInfo(@JsonProperty("make") String carMake, @JsonProperty("model") String carModel, @JsonProperty("price") String carPrice, @JsonProperty("leaseLength") String carLease) {
		make = carMake;
		model = carModel;
		price = carPrice;
		leaseLength = carLease;
	}
	
	
	/**
	 * @return make car make
	 */
	public String getMake() {
		return make;
	}
	/**
	 * @param make
	 */
	public void setMake(String make) {
		this.make = make;
	}
	/**
	 * @return model car model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return leaseLength length of lease
	 */
	public String getLeaseLength() {
		return leaseLength;
	}
	/**
	 * @param leaseLength
	 */
	public void setLeaseLength(String leaseLength) {
		this.leaseLength = leaseLength;
	}
	/**
	 * @return price car price
	 */
	public String getPrice() {
		return price;
	}
	/**
	 * @param price
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	
	/**
	 * display car information in formatted style
	 * @return formmattedString 
	 */
	@Override
	public String toString() {
		String formattedString = String.join("\t", make, model, price, leaseLength);
		return formattedString;

	}
	

}
