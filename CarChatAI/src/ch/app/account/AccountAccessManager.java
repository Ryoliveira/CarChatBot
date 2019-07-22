package ch.app.account;

import java.util.List;
import java.util.Scanner;

import ch.app.file.UserInfoRepository;
import ch.app.file.UserInfoSerRepository;
import ch.app.models.User;


public class AccountAccessManager {

	private Scanner input;
	private UserInfoRepository userRepo;
	private User userProfile;

	public AccountAccessManager() {
		input = new Scanner(System.in);
		userRepo = new UserInfoSerRepository();
		userProfile = new User();
	}

	/**
	 * Create new user profile
	 * 
	 * @return newUser newly created profile
	 */
	public User createProfile() {
		InfoRecorder recorder = new InfoRecorder();
		User newUser = recorder.recordUserInfo();
		System.out.println("Your new username is " + newUser.getUsername());
		userRepo.addUser(newUser);
		return newUser;
	}

	/**
	 * Return corresponding user profile if credentials match, create new profile
	 * otherwise
	 * 
	 * @return user User profile if found
	 * @return newUser new profile if created
	 */
	public User logIn() {
		Boolean access = false;
		Boolean correctPass = false;
		while (!access) {
			userProfile = findUser();
			if (userProfile != null) {
				while (!correctPass) {
					correctPass = passwordMatch();
					if (correctPass) {
						access = true;
					} else {
						System.out.println("Password did not match. Try again.");
					}
				}
			} else {
				System.out.println("Username not found.");
				System.out.println("Would you like to create a new profile?");
				String ans = input.nextLine();
				if (ans.equalsIgnoreCase("y")) {
					userProfile = createProfile();
					access = true;
				}
			}
		}
		return userProfile;
	}

	/**
	 * Search user list for given username
	 * 
	 * @return user User profile if found, null otherwise
	 */
	private User findUser() {
		List<User> userList = userRepo.load();
		System.out.print("Enter Username: ");
		String username = input.nextLine();
		for (User user : userList) {
			if (username.equalsIgnoreCase(user.getUsername())) {
				return user;
			}
		}
		return null;
	}

	
	/**
	 * Checks whether given password matches hashed password
	 * 
	 * @return true is given pw matches hashed pw, false otherwise
	 */
	private boolean passwordMatch() {
		PassManager pwManager = new PassManager();
		System.out.print("Enter Password: ");
		String pass = input.nextLine();
		if (pwManager.validatePassword(pass, userProfile.getPassword())) {
			return true;
		} else {
			return false;
		}

	}

}
