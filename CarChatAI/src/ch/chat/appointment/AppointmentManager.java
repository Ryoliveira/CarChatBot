package ch.chat.appointment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import ch.chat.car.CarInfo;
import ch.chat.file.AppointmentJsonRepository;
import ch.chat.file.AppointmentRepository;
import ch.chat.file.CarInventoryRepository;
import ch.chat.file.InventoryRepository;
import ch.chat.message.MessageAnalyzer;

public class AppointmentManager {
	private static Scanner input = new Scanner(System.in);
	private static List<Appointment> appointmentList;

	private String day;
	private String time;
	private CarInfo car;

	/**
	 * Get users car budget
	 * 
	 * @return budget users car budget
	 */
	public int getBudget() {
		// get budget for car rental
		boolean valid = false;
		int budget = 0;
		while (!valid) {
			System.out.print("Enter rental budget: ");
			try {
				budget = Integer.parseInt(MessageAnalyzer.checkForInsult(input.nextLine()));
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
	public List<CarInfo> getCarsInBudget() {
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
	public List<String> getAvailableTimes() {
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
	public List<String> getNonVacantTimeSlots(String date) {
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
	public void displayAvailableTimes(List<String> availableTimes) {
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
	public CarInfo selectCar() {
		boolean valid = false;
		CarInfo car = null;
		List<CarInfo> cars = getCarsInBudget();
		displayCarsInBudget(cars);
		System.out.println("Please select a car from the list.");
		while (!valid) {
			try {
				int selection = Integer.parseInt(MessageAnalyzer.checkForInsult(input.nextLine()));
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
	public String selectDay() {
		boolean valid = false;
		int userChoice = 0;
		String chosenDay = null;
		List<String> availableDays = getAppointmentsDays();
		displayAvailableDays(availableDays);
		System.out.println("Which day would work for you?");
		while (!valid) {
			try {
				userChoice = Integer.parseInt(MessageAnalyzer.checkForInsult(input.nextLine()));
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
	public String selectTime() {
		String selectedTime = null;
		boolean valid = false;
		List<String> openTimes = getAvailableTimes();
		displayAvailableTimes(openTimes);
		System.out.println("Please select a time listed.");
		
		while (!valid) {
			try {
				int userChoice = Integer.parseInt(MessageAnalyzer.checkForInsult(input.nextLine()));
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
	public void appointmentSetup(UUID userID) {
		AppointmentRepository appRepo = new AppointmentJsonRepository();
		appointmentList = appRepo.loadAppointments();
		car = selectCar();
		day = selectDay();
		time = selectTime();
		Appointment newApp = new Appointment(day, time, userID, car);
		appRepo.save(newApp);
		System.out.println("Appointment Created!");
		System.out.println(newApp);
	}

}
