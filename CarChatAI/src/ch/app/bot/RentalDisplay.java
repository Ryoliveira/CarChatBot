package ch.app.bot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ch.app.file.AppointmentJsonRepository;
import ch.app.file.AppointmentRepository;
import ch.app.file.CarInventoryRepository;
import ch.app.file.InventoryRepository;
import ch.app.models.Appointment;
import ch.app.models.CarInfo;

public class RentalDisplay {
	
	private UUID userId;
	private AppointmentRepository appRepo;
	private List<Appointment> appointmentList;
	

	public RentalDisplay(UUID userId) {
		this.userId = userId; 
		appRepo = new AppointmentJsonRepository();
		appointmentList = appRepo.loadAllAppointments();
	}
	
	
	/**
	 * Displays current users appointments
	 */
	public void displayUserAppointments() {
		List<Appointment> userApps = getUserAppointments();
		int i = 1;
		for (Appointment app : userApps) {
			System.out.println(i++ + ")");
			System.out.println(app + "\n");
		}
	}

	/**
	 * Return list of all user specific appointments
	 * 
	 * @return List of user appointments
	 */
	public List<Appointment> getUserAppointments() {
		return appRepo.loadUser(userId);
	}
	
	/**
	 * Return list of cars within users budget
	 * 
	 * @return carsAvailable cars within users budget
	 */
	public List<CarInfo> getCarsInBudget(int budget) {
		InventoryRepository carRepo = new CarInventoryRepository();
		List<CarInfo> carsAvailable = new ArrayList<>();
		List<CarInfo> carInv = carRepo.loadInventory();
		for (CarInfo car : carInv) {
			int carPrice = Integer.parseInt(car.getPrice());
			if (carPrice <= budget) {
				carsAvailable.add(car);
			}
		}
		return carsAvailable;
	}
	
	/**
	 * Get available appointment days
	 * 
	 * @return availableDays list of availableDays
	 */
	public List<String> getAppointmentsDays() {
		List<String> availableDays = new ArrayList<>();
		int twoWeeks = 14;
		Date date = new Date();
		LocalDate time = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		for (int i = 0; i < twoWeeks; i++) {
			String weekDay = time.getDayOfWeek().toString();
			int month = time.getMonthValue();
			int day = time.getDayOfMonth();
			int year = time.getYear();
			String offerDate = weekDay + " : " + month + "/" + day + "/" + year;
			int dayOfWeek = time.getDayOfWeek().getValue();
			if (dayOfWeek != 6 && dayOfWeek != 7) {// Closed on weekends
				if (getNonVacantTimeSlots(offerDate).size() != 9) {
					availableDays.add(offerDate);
				}
			}
			time = time.plusDays(1);
		}
		return availableDays;
	}

	/**
	 * Get list of open time slots
	 * 
	 * @return openTimes List of open time slots
	 */
	public List<String> getAvailableTimes(String day) {
		List<String> openTimes = new ArrayList<>();
		List<String> nonVacantTimes = getNonVacantTimeSlots(day);
		String meridiem = "am";
		int totalHours = 9;
		int startTime = 9;
		for (int i = 0; i < totalHours; i++) {
			if (startTime == 12) {
				meridiem = "pm";
			}
			String time = startTime++ + ":00" + meridiem;
			if (!nonVacantTimes.contains(time)) {
				openTimes.add(time);
			} else {
				i--;
				totalHours--;
			}
			if (startTime == 13) {
				startTime = 1;
			}
		}
		return openTimes;
	}

	/**
	 * Get non vacant time slots for specific date
	 * 
	 * @param date Specific date to look up time slots
	 * @return nonVacantTimes List of non vacant time slots
	 */
	private List<String> getNonVacantTimeSlots(String date) {
		List<String> nonVacantTimes = new ArrayList<>();
		for (Appointment app : appointmentList) {
			if (app.getDate().equals(date)) {
				nonVacantTimes.add(app.getTime());
			}
		}
		return nonVacantTimes;
	}

	/**
	 * Display all cars in the given list
	 * 
	 * @param cars List of cars to display
	 */
	public void displayCarsInBudget(List<CarInfo> cars) {
		System.out.println("#\tMake\tModel\tPrice\tLength");
		System.out.println("--------------------------------");
		int i = 1;
		for (CarInfo car : cars) {
			System.out.println(i++ + ")\t" + car.toString());
		}
	}

	/**
	 * Display all days with open time slots
	 * 
	 * @param availableDays List of days with open time slots
	 */
	public void displayAvailableDays(List<String> availableDays) {
		int choice = 1;
		System.out.println("\nAvailable days in the next two weeks");
		for (String day : availableDays) {
			System.out.println(choice++ + ") " + day);
		}
	}

	/**
	 * Display all available time slots
	 * 
	 * @param availableTimes List of open time slots
	 */
	public void displayAvailableTimes(List<String> availableTimes) {
		int i = 1;
		System.out.println("\nAvailable appointment hours");
		for (String time : availableTimes) {
			System.out.println(i++ + ") " + time);
		}
	}
	
	/**
	 * Display newly created Appointment
	 * @param app created Appointment
	 */
	public void displayCreatedApp(Appointment app) {
		System.out.println("\nAppointment Created!");
		System.out.println(app + "\n");
	}
	
	/**
	 * Displays option menu to user
	 * @param menu list of options
	 */
	public void displayMenu(List<String> menu) {
		System.out.println();
		for(int i=0;i<menu.size();i++) {
			System.out.println((i+1) +") " + menu.get(i));
		}
		System.out.println();
	}

}
