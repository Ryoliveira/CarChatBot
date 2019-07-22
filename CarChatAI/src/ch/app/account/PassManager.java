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
		// Password must be between 8 and 16 digits and include at least one digit.
		boolean passwordMatch = false;
		String password = "";
		while (!passwordMatch) {
			System.out.print("Please enter a password: ");
			password = getPassword();
			System.out.print("Please re-enter password: ");
			String repeatedPass = getPassword();
			if (password.equals(repeatedPass)) {
				passwordMatch = true;
			}else {
				System.out.println("Passwords did not match! Try Again.");
			}
		}
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}

	
	private String getPassword() {
		String passwordPat = "^(?=.*\\d).{8,16}$";
		boolean valid = false;
		String password = "";
		while (!valid) {
			password = input.nextLine();
			if (password.matches(passwordPat)) {
				valid = true;
			} else {
				System.out.println("Password must be between 8-16 characters. Try Again.");
			}
		}
		return password;
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
