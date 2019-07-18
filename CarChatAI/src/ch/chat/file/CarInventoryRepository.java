package ch.chat.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.chat.car.CarInfo;

public class CarInventoryRepository implements InventoryRepository{
	
	String FILE_NAME = "./Cars.txt";

	/**
	 * Save list of cars
	 * 
	 *@param cars List of cars to be saved
	 */
	@Override
	public void save(List<CarInfo> cars){
		try {
			FileWriter fw = new FileWriter(new File(FILE_NAME));
			for(CarInfo car : cars) {
				fw.write(car.getMake() + " " + car.getModel() + 
						";" + car.getPrice() + 
						";" + car.getLeaseLength() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load and return list of available cars
	 * 
	 * @return carInventory current inventory of cars
	 */
	@Override
	public List<CarInfo> load() {
		List<CarInfo> carInventory = new ArrayList<>();
		File file = new File(FILE_NAME);
		try {
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				String[] info = line.split(";");
				String[] makeModel = info[0].split(" ");
				CarInfo newCar = new CarInfo(makeModel[0], makeModel[1], info[1], info[2]);
				carInventory.add(newCar);
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("File not found. Creating new list.");
			return carInventory;
		}
		return carInventory;
	}
	

}
