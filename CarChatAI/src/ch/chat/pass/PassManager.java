package ch.chat.pass;

import java.util.Scanner;

public class PassManager {
	private static Scanner input = new Scanner(System.in);
	
	/**
	 * Record new password from user
	 * 
	 * @return hashedPass hashed user password
	 */
	public static String createPassword() {
		// Password must be between 8 and 16 digits and include at least one digit.
		String passwordPat = "^(?=.*\\d).{8,16}$";
		String password = "";
		String repeatedPass = "";
		while(password.isEmpty()) {
			System.out.print("Please enter a password: ");
			password = input.nextLine();
			if(password.matches(passwordPat)) {
				System.out.print("Please re-enter password: ");
				repeatedPass = input.nextLine();
				if(!password.equals(repeatedPass)) {
					password = "";
					System.out.println("Passwords did not match! Try Again.");
				}
			}else {
				password = "";
			}
		}
		String hashedPass = BCrypt.hashpw(password, BCrypt.gensalt(12));
		return hashedPass;
	}
	
	/**
	 * Match hashed password with user provided password
	 * 
	 * @param providedPass password provided by user
	 * @param hashedPass stored hashed password of given user
	 * @return true if password matches, false otherwise
	 */
	public static Boolean validatePassword(String providedPass, String hashedPass) {
		return BCrypt.checkpw(providedPass, hashedPass);
	}
	

}
