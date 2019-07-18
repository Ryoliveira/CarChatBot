package ch.chat.file;

import java.util.List;

import ch.chat.car.CarInfo;

public interface InventoryRepository {
	void save(List<CarInfo> cars);
	List<CarInfo> load();
}
