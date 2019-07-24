package ch.app.bot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import ch.app.file.AppointmentJsonRepository;
import ch.app.file.AppointmentRepository;
import ch.app.file.CarInventoryRepository;
import ch.app.file.InventoryRepository;
import ch.app.models.Appointment;
import ch.app.models.CarInfo;

public class AppointmentManager {
	private Scanner input = new Scanner(System.in);
	private List<Appointment> appointmentList;
	private AppointmentRepository appRepo;
	private MessageAnalyzer msgAnalyzer;
	private UUID userID;
	private String day;
	private String time;
	private CarInfo car;

	public AppointmentManager(UUID userID) {
		appRepo = new AppointmentJsonRepository();
		appointmentList = appRepo.loadAppointments();
		msgAnalyzer = new MessageAnalyzer();
		this.userID = userID;
	}

	/**
	 * Displays current users appointments
	 */
	private void displayUserAppointments() {
		List<Appointment> userApps = getUserAppointments();
		int i = 1;
		for (Appointment app : userApps) {
			System.out.println(i++ + ")");
			System.out.println(app);
			System.out.println();
		}
	}

	private List<Appointment> getUserAppointments() {
		return appRepo.load(userID);
	}

	/**
	 * Get users car budget
	 * 
	 * @return budget users car budget
	 */
	private int getBudget() {
		// get budget for car rental
		boolean valid = false;
		int budget = 0;
		while (!valid) {
			System.out.print("Enter rental budget: ");
			try {
				budget = Integer.parseInt(msgAnalyzer.checkForInsult(input.nextLine()));
				valid = true;
			} catch (InputMismatchException | NumberFormatException e) {
				System.out.println("Please enter a valid number.");
			}
		}
		return budget;
	}

	/**
	 * Return list of cars within users budget
	 * 
	 * @return carsAvailable cars within users budget
	 */
	private List<CarInfo> getCarsInBudget() {
		// display cars within users budget
		InventoryRepository carRepo = new CarInventoryRepository();
		List<CarInfo> carsAvailable = new ArrayList<>();
		List<CarInfo> carInv = carRepo.load();
		int budget = getBudget();
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
	private List<String> getAppointmentsDays() {
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
	private List<String> getAvailableTimes() {
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
	private void displayCarsInBudget(List<CarInfo> cars) {
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
	private void displayAvailableDays(List<String> availableDays) {
		int choice = 1;
		System.out.println("Available days in the next two weeks");
		for (String day : availableDays) {
			System.out.println(choice++ + ") " + day);
		}
	}

	/**
	 * Display all available time slots
	 * 
	 * @param availableTimes List of open time slots
	 */
	private void displayAvailableTimes(List<String> availableTimes) {
		int i = 1;
		System.out.println("Available appointment hours");
		for (String time : availableTimes) {
			System.out.println(i++ + ") " + time);
		}
	}

	/**
	 * Display cars within user budget and let user select car for appointment
	 * 
	 * @return car selected CarInfo object
	 */
	private CarInfo selectCar() {
		boolean valid = false;
		CarInfo car = null;
		List<CarInfo> cars = getCarsInBudget();
		displayCarsInBudget(cars);
		System.out.println("Please select a car from the list.");
		while (!valid) {
			try {
				int selection = Integer.parseInt(msgAnalyzer.checkForInsult(input.nextLine()));
				car = cars.get(selection - 1);
				valid = true;
			} catch (InputMismatchException | IndexOutOfBoundsException | NumberFormatException e) {
				System.out.println("Please enter a number between 1 - " + cars.size());
			}
		}
		return car;
	}

	/**
	 * Display available appointment days and let user select day
	 * 
	 * @return chosenDay Day of appointment
	 */
	private String selectDay() {
		boolean valid = false;
		int userChoice = 0;
		String chosenDay = null;
		List<String> availableDays = getAppointmentsDays();
		displayAvailableDays(availableDays);
		System.out.println("Which day would work for you?");
		while (!valid) {
			try {
				userChoice = Integer.parseInt(msgAnalyzer.checkForInsult(input.nextLine()));
				chosenDay = availableDays.get(userChoice - 1);
				valid = true;
			} catch (InputMismatchException | IndexOutOfBoundsException | NumberFormatException e) {
				System.out.println("Choice must be between 1 -" + availableDays.size());
			}
		}
		return chosenDay;
	}

	/**
	 * Display available appointment times and let user select one
	 * 
	 * @return selectedTime User selected time of appointment
	 */
	private String selectTime() {
		String selectedTime = null;
		boolean valid = false;
		List<String> openTimes = getAvailableTimes();
		displayAvailableTimes(openTimes);
		System.out.println("Please select a time listed.");

		while (!valid) {
			try {
				int userChoice = Integer.parseInt(msgAnalyzer.checkForInsult(input.nextLine()));
				selectedTime = openTimes.get(userChoice - 1);
				valid = true;
			} catch (InputMismatchException | IndexOutOfBoundsException | NumberFormatException e) {
				System.out.println("Choice must be between 1 - " + openTimes.size());
			}
		}
		return selectedTime;
	}

	/**
	 * Using specific user id, create a new appointment and save to file
	 * 
	 * @param userID Unique user ID
	 */
	public void setUpAppointment() {
		car = selectCar();
		day = selectDay();
		time = selectTime();
		Appointment newApp = new Appointment(day, time, userID, car);
		appRepo.save(newApp);
		System.out.println("Appointment Created!");
		System.out.println(newApp);
	}

	
	/**
	 * Let user choose to remove or edit an existing appointment
	 */
	public void modifyAppointment() {
		System.out.println("1)View Appointment(s) \n2)Edit Appointment \n3)Remove Appointment \n4)Cancel");
		System.out.print("Enter a selection: ");
		try {
			int userChoice = Integer.parseInt(input.nextLine());
			if (userChoice == 1) {
				displayUserAppointments();
			} else if (userChoice == 2) {
				editAppointment();
			}else if(userChoice == 3){
				removeAppointment();
			}else if (userChoice == 4) {
				return;
			} else {
				throw new ArrayIndexOutOfBoundsException();
			}
		} catch (ArrayIndexOutOfBoundsException | InputMismatchException e) {
			System.out.println("Enter a number between 1-3");
			modifyAppointment();
		}
	}

	/**
	 * Let user choose which appointment to edit
	 */
	private void editAppointment() {
		int userChoice = -1;
		Appointment editedApp = null;
		boolean finished = false;
		List<Appointment> userApps = getUserAppointments();
		while (!finished) {
			displayUserAppointments();
			System.out.println("Select appointment to edit");
			try {
				userChoice = Integer.parseInt(msgAnalyzer.checkForInsult(input.nextLine())) - 1;
				editedApp = editFields(userApps.get(userChoice));
			} catch (InputMismatchException |IndexOutOfBoundsException e) {
				System.out.println("Please enter a number between 1-" + userApps.size());
				continue;
			}
			System.out.println("Save Changes?(Y/N)");
			String choice = input.nextLine();
			if (choice.equalsIgnoreCase("Y")) {
				appRepo.update(userID, userChoice, editedApp);
				finished = true;
			}
		}
	}

	
	/**
	 * Let user choose which field to edit
	 * 
	 * @param app Appointment to edited
	 * @return app edited Appointment
	 */
	private Appointment editFields(Appointment app) {
		int userChoice = -1;
		boolean finished = false;
		while (!finished) {
			System.out.println("Select one of the following "
					+ "\n1)Date of appointment \n2)Time of appointment \n3)Car to view");
			try {
				userChoice = Integer.parseInt(msgAnalyzer.checkForInsult(input.nextLine()));
				if(userChoice < 1 || userChoice > 3) throw new IndexOutOfBoundsException();
			}catch (InputMismatchException | IndexOutOfBoundsException e) {
				System.out.println("Please enter a number between 1-3");
				continue;
			}
			if(userChoice == 1) app.setDate(selectDay());
			else if(userChoice == 2) app.setTime(selectTime());
			else if(userChoice == 3) app.setCarDetail(selectCar());
			System.out.println("Change another field?(Y/N)");
			if(!msgAnalyzer.checkForInsult(input.nextLine()).equalsIgnoreCase("Y")) {
				finished = true;
			}
		}
		return app;
	}
	
	
	/**
	 * Let user choose which Appointment to remove
	 */
	private void removeAppointment() {
		int userChoice = -1;
		boolean deleted = false;
		List<Appointment> userApps = getUserAppointments();
		while (!deleted) {
			displayUserAppointments();
			System.out.println("Select appointment to delete");
			try {
				userChoice = Integer.parseInt(input.nextLine()) - 1;
			} catch (InputMismatchException | IndexOutOfBoundsException e) {
				System.out.println("Please enter a number between 1-" + userApps.size());
				continue;
			}
			System.out.println("Are you sure you want to delete?(Y/N)");
			if (msgAnalyzer.checkForInsult(input.nextLine()).equalsIgnoreCase("Y")) {
				appRepo.remove(userID, userChoice);
				System.out.println("Appointment deleted.");
				deleted = true;
			}
		}
	}

}
