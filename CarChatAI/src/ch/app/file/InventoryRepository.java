package ch.app.file;

import java.util.List;

import ch.app.models.CarInfo;

public interface InventoryRepository {
	void save(List<CarInfo> cars);
	List<CarInfo> load();
}
