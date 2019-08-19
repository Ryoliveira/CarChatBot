package ch.app.file;

import java.util.List;

import ch.app.models.CarInfo;

public interface InventoryRepository {
	void saveInventory(List<CarInfo> cars);

	List<CarInfo> loadInventory();
}
