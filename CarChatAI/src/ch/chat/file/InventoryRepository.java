package ch.chat.file;

import java.util.List;

import ch.chat.models.CarInfo;

public interface InventoryRepository {
	void save(List<CarInfo> cars);
	List<CarInfo> load();
}
