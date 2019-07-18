package ch.chat.file;

import java.util.Map;

public interface WordRepository {
	Map<String, Integer> load();
}
