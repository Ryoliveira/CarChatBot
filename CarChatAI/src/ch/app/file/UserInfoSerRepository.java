package ch.app.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import ch.app.models.User;

public class UserInfoSerRepository implements UserInfoRepository {
	final private String FILE_NAME = "./Users.ser";

	/**
	 * Save list of users to file
	 * 
	 * @param users List of users
	 */
	@Override
	public void save(List<User> users) {
		try {
			FileOutputStream fos = new FileOutputStream(FILE_NAME);
			ObjectOutputStream outputFile = new ObjectOutputStream(fos);
			for (User user : users) {
				outputFile.writeObject(user);
			}
			outputFile.flush();
			outputFile.close();
		} catch (IOException e) {
			e.getMessage();
		}

	}

	/**
	 * Load and return list of users
	 * 
	 * @return userList List of users
	 */
	@Override
	public List<User> load() {
		List<User> userList = new ArrayList<>();
		try {
			FileInputStream inputStream = new FileInputStream(FILE_NAME);
			ObjectInputStream objectInputFile = new ObjectInputStream(inputStream);
			User loadedUser;
			while ((loadedUser = (User) objectInputFile.readObject()) != null) {
				userList.add(loadedUser);
			}
			objectInputFile.close();
		} catch (IOException | ClassNotFoundException e) {
			e.getMessage();
		}
		return userList;
	}

	/**
	 * Adds new user to user list file
	 *
	 * @param newUser user to be added
	 *
	 */
	@Override
	public void addUser(User newUser) {
		List<User> userList = load();
		userList.add(newUser);
		save(userList);
	}

}
