package ch.app.models;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public class User implements Serializable {

	private UUID id;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private String age;

	
	/**
	 * @param first First name of user
	 * @param last Last name of user
	 * @param pw User password
	 * @param userAge Age of user
	 * @param username Username of user
	 */
	public User(String first, String last, String pw, String userAge, String username) {
		this.id = this.id != null ? this.id : UUID.randomUUID();
		this.firstName = first;
		this.lastName = last;
		this.password = pw;
		this.age = userAge;
		this.username = username;
	}
	
	/**
	 * 
	 */
	public User() {
		this(null, null, null, null, null);
	}
	
	/**
	 * @return id user ID
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id user ID
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return fullname Users first and last name
	 */
	public String getFullName() {
		String fullname = String.join(" ", this.firstName, this.lastName);
		return fullname;
	}

	/**
	 * @return firstName users first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName 
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return lastName users last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return age user age
	 */
	public String getAge() {
		return age;
	}

	/**
	 * @return username user username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return password user password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password 
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param age
	 */
	public void setAge(String age) {
		this.age = age;
	}
}
