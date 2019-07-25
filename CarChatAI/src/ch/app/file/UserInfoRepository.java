package ch.app.file;

import java.util.List;

import ch.app.models.User;

public interface UserInfoRepository {
	void save(List<User> users);

	List<User> load();

	void addUser(User newUser);
}
