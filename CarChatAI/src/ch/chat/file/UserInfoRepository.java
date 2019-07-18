package ch.chat.file;

import java.util.List;
import ch.chat.user.User;

public interface UserInfoRepository {
	void save(List<User> users);
	List<User> load();
	void addUser(User newUser);
}
