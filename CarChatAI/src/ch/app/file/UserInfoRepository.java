package ch.app.file;

import java.util.List;

import ch.app.models.User;

public interface UserInfoRepository {
	void saveUsers(List<User> users);

	List<User> loadUsers();

	void addUser(User newUser);
}
