package ch.chat.account;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import ch.chat.file.UserInfoRepository;
import ch.chat.file.UserInfoSerRepository;
import ch.chat.pass.PassManager;
import ch.chat.user.User;

public class InfoRecorder {
	final Scanner input = new Scanner(System.in);

	/**
	 * Record basic user info and return new profile
	 * 
	 * @return newUser created profile
	 */
	public User getUserInfo() {
		String username = getUsername();
		String firstName = getName("first");
		String lastName = getName("last");
		String age = getAge();
		String password = PassManager.createPassword();
		User newUser = new User(firstName, lastName, password, age, username);
		return newUser;
	}

	/**
	 * Create user username
	 * 
	 * @return username User created username
	 */
	public String getUsername() {
		// TODO Create username pattern
		String usernamePat = "^(?=.{8,20}$)[a-zA-Z0-9._]+$";
		UserInfoRepository userRepo = new UserInfoSerRepository();
		List<User> userList = userRepo.load();
		String username = null;
		boolean valid = false;
		boolean duplicate = false;
		System.out.println("Please enter a username");
		while (!valid) {
			duplicate = false;
			username = input.nextLine();
			if(!username.matches(usernamePat)) {
				System.out.println("username must be between 8-20 characters");
				continue;
			}
			for (User user : userList) {
				if (user.getUsername().equalsIgnoreCase(username)) {
					System.out.println("Username already in use, please try again.");
					duplicate = true;
					break;
				}
			}
			if (!duplicate && username.matches(usernamePat)) {
				valid = true;
			}
		}
		return username;
	}

	/**
	 * Get users name
	 * 
	 * @param firstLast First or Last String
	 * @return name Users name
	 */
	public String getName(String firstLast) {
		String name = null;
		while (name == null) {
			System.out.print("Please enter " + firstLast + " name: ");
			name = input.nextLine();
			if (!name.matches("^[a-zA-Z]+$")) {
				name = null;
				System.out.println("Please enter a valid name.");
			}
		}
		return name;
	}

	/**
	 * Get users age
	 * 
	 * @return age Users age as String
	 */
	public String getAge() {
		int age = 0;
		while (age == 0) {
			System.out.print("Please enter age: ");
			try {
				age = Integer.parseInt(input.nextLine());
				if (age < 1 || age > 100) {
					age = 0;
					throw new InputMismatchException();
				}
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid age between 18-100.");
			}
		}
		return String.valueOf(age);
	}

}
