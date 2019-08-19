package ch.app.account;

import java.util.Scanner;

public class PassManager {

	private Scanner input;

	public PassManager() {
		input = new Scanner(System.in);
	}

	/**
	 * Record new password from user
	 * 
	 * @return hashedPass hashed user password
	 */
	public String createPassword() {
		String password = getPassword();
		retypePassword(password);
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}

	/**
	 * Create user password using password pattern
	 * 
	 * @return password user created password
	 */
	private String getPassword() {
		boolean valid = false;
		String password = "";
		while (!valid) {
			System.out.print("Please enter a password: ");
			password = input.nextLine();
			valid = isValidPassword(password);
			if (!valid) {
				System.out.println("Password must be between 8-16 characters and include 1 digit. Try Again.");
			}
		}
		return password;
	}

	/**
	 * Have user retype password and match
	 * 
	 * @param true is retyped password matches original, false otherwise
	 */
	private void retypePassword(String password) {
		boolean match = false;
		String retypedPass = "";
		while (!match) {
			System.out.print("Please repeat password: ");
			retypedPass = input.nextLine();
			if (password.equals(retypedPass)) {
				match = true;
			} else {
				System.out.println("Passwords did not match, try again.");
			}
		}
	}

	/**
	 * Check if password matches password pattern
	 * 
	 * @param password User provided password
	 * @return true if password matches patter, false otherwise
	 */
	private boolean isValidPassword(String password) {
		// Password must be between 8 and 16 digits and include at least one digit.
		String passwordPat = "^(?=.*\\d).{8,16}$";
		return password.matches(passwordPat);
	}

	/**
	 * Match hashed password with user provided password
	 * 
	 * @param providedPass password provided by user
	 * @param hashedPass   stored hashed password of given user
	 * @return true if password matches, false otherwise
	 */
	public Boolean validatePassword(String providedPass, String hashedPass) {
		return BCrypt.checkpw(providedPass, hashedPass);
	}

}
