package ch.chat.account;

import java.util.List;
import java.util.Scanner;

import ch.chat.file.UserInfoRepository;
import ch.chat.file.UserInfoSerRepository;
import ch.chat.pass.PassManager;
import ch.chat.user.User;

public class AccountAccessManager {

	final static UserInfoRepository userRepo = new UserInfoSerRepository();

	/**
	 * Create new user profile
	 * 
	 * @return newUser newly created profile
	 */
	public static User createProfile() {
		InfoRecorder recorder = new InfoRecorder();
		User newUser = recorder.getUserInfo();
		System.out.println("Your new username is " + newUser.getUsername());
		userRepo.addUser(newUser);
		return newUser;
	}

	/**
	 * Return corresponding user profile if credentials match, create new profile otherwise
	 * 
	 * @return user User profile if found
	 * @return newUser new profile if created
	 */
	public static User logIn() {
		Boolean foundProfile = false;
		Boolean correctPass = false;
		List<User> userList = userRepo.load();
		Scanner input = new Scanner(System.in);
		while (!foundProfile) {
			System.out.print("Enter Username: ");
			String username = input.nextLine();
			for (User user : userList) {
				if (username.equalsIgnoreCase(user.getUsername())) {
					while (!correctPass) {
						System.out.print("Enter Password: ");
						String pass = input.nextLine();
						if (PassManager.validatePassword(pass, user.getPassword())) {
							return user;
						} else {
							System.out.println("Password incorrect");
						}
					}
				}
			}
			System.out.println("Username not found.");
			System.out.println("Would you like to create a new profile?");
			String ans = input.nextLine();
			if(ans.equalsIgnoreCase("y")) {
				User newUser = createProfile();
				return newUser;
			}
		}
		return null;
	}

}
