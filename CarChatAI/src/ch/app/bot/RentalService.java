package ch.app.bot;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import ch.app.file.AppointmentJsonRepository;
import ch.app.file.AppointmentRepository;
import ch.app.models.Appointment;
import ch.app.models.CarInfo;

public class RentalService {

	private AppointmentRepository appRepo;
	private RentalDisplay appDisplay;
	private MessageAnalyzer msgAnalyzer;
	private Scanner scanner;
	private UUID userID;
	private String day;
	private String time;
	private CarInfo car;

	public RentalService(UUID userID) {
		scanner = new Scanner(System.in);
		appRepo = new AppointmentJsonRepository();
		appDisplay = new RentalDisplay(userID);
		msgAnalyzer = new MessageAnalyzer();
		this.userID = userID;
	}

	/**
	 * Get users car budget
	 * 
	 * @return budget users car budget
	 */
	private int getBudget() {
		boolean isValid = false;
		int budget = 0;
		while (!isValid) {
			System.out.print("Enter rental budget: ");
			try {
				budget = Integer.parseInt(msgAnalyzer.removeInsults(scanner.nextLine()));
				isValid = true;
				if (budget < 0)
					throw new IllegalArgumentException();
			} catch (InputMismatchException | IllegalArgumentException e) {
				System.out.println("Please enter a valid number above 0.");
			}
		}
		return budget;
	}

	/**
	 * Display cars within user budget and let user select car for appointment
	 * 
	 * @return car selected CarInfo object
	 */
	private CarInfo selectCar() {
		int budget = getBudget();
		List<CarInfo> cars = appDisplay.getCarsInBudget(budget);
		appDisplay.displayCarsInBudget(cars);
		int choice = getChoice(cars.size(), "car", "view");
		return cars.get(choice);
	}

	/**
	 * Display available appointment days and let user select day
	 * 
	 * @return chosenDay Day of appointment
	 */
	private String selectDay() {
		int userChoice = -1;
		List<String> availableDays = appDisplay.getAppointmentsDays();
		appDisplay.displayAvailableDays(availableDays);
		userChoice = getChoice(availableDays.size(), "date", "come in");
		return availableDays.get(userChoice);
	}

	/**
	 * Display available appointment times and let user select one
	 * 
	 * @return selectedTime User selected time of appointment
	 */
	private String selectTime() {
		List<String> openTimes = appDisplay.getAvailableTimes(day);
		appDisplay.displayAvailableTimes(openTimes);
		int userChoice = getChoice(openTimes.size(), "time", "come in");
		return openTimes.get(userChoice);
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
		appDisplay.displayCreatedApp(newApp);
	}

	/**
	 * Let user choose to remove or edit an existing appointment
	 */
	public void selectAppointmentAction() {
		appDisplay.displayMenu(Constants.MODIFY_OPTIONS);
		int userChoice = getChoice(Constants.MODIFY_OPTIONS.size(), "action", "continue");
		switch (userChoice) {
			case 0:
				appDisplay.displayUserAppointments();
				break;
			case 1:
				editAppointment();
				break;
			case 2:
				removeAppointment();
				break;
			default:
				return;
		}

	}

	/**
	 * Let user choose which appointment to edit
	 */
	private void editAppointment() {
		int userChoice = -1;
		boolean isFinished = false;
		List<Appointment> userApps = appDisplay.getUserAppointments();
		while (!isFinished) {
			appDisplay.displayUserAppointments();
			userChoice = getChoice(userApps.size(), "appointment", "edit");
			Appointment editedApp = editAppointmentFields(userApps.get(userChoice));
			if (getConfirmation("Save Changes?(Y/N)")) {
				appRepo.update(userID, userChoice, editedApp);
				isFinished = true;
			}
		}
	}

	/**
	 * Let user choose which field to edit
	 * 
	 * @param app Appointment to edited
	 * @return app edited Appointment
	 */
	private Appointment editAppointmentFields(Appointment app) {
		int userChoice = -1;
		boolean isFinished = false;
		while (!isFinished) {
			appDisplay.displayMenu(Constants.EDIT_OPTIONS);
			userChoice = getChoice(Constants.EDIT_OPTIONS.size(), "field", "edit");
			switch (userChoice) {
			case 0:
				app.setDate(selectDay());
				break;
			case 1:
				app.setTime(selectTime());
				break;
			case 2:
				app.setCarDetail(selectCar());
				break;
			}
			isFinished = !getConfirmation("Change another field?(Y/N)");
		}
		return app;
	}

	/**
	 * Let user choose which Appointment to remove
	 */
	private void removeAppointment() {
		int userChoice = -1;
		boolean isFinished = false;
		List<Appointment> userApps = appDisplay.getUserAppointments();
		while (!isFinished) {
			appDisplay.displayUserAppointments();
			userChoice = getChoice(userApps.size(), "appointment", "delete");
			if (getConfirmation("Are you sure you want to delete?(Y/N)")) {
				appRepo.remove(userID, userChoice);
				System.out.println("Appointment deleted.");
				isFinished = true;
			}
			isFinished = !getConfirmation("Select another appointment to delete?(Y/N)");
		}
	}

	/**
	 * Get user choice from range 1 - # of choices
	 * 
	 * @param choices amount of choices
	 * @param topic   topic of choices
	 * @param action  action to be performed
	 * @return user choice as int
	 */
	private int getChoice(int choices, String topic, String action) {
		int userChoice = -1;
		boolean isValid = false;
		System.out.printf("\nSelect %s to %s:", topic, action);
		while (!isValid) {
			try {
				userChoice = Integer.parseInt(msgAnalyzer.removeInsults(scanner.nextLine())) - 1;
				if (userChoice < 0 || userChoice > choices - 1) {
					throw new IndexOutOfBoundsException();
				}
			} catch (InputMismatchException | IndexOutOfBoundsException | NumberFormatException e) {
				System.out.println("Please enter a number between 1-" + choices);
				continue;
			}
			isValid = true;
		}
		return userChoice;
	}

	/**
	 * Print prompt to user and detect confirmation
	 * 
	 * @param prompt prompt to ask user
	 * @return true if confirmation is detected, false otherwise
	 */
	public boolean getConfirmation(String prompt) {
		System.out.println(prompt);
		String userAnswer = msgAnalyzer.removeInsults(scanner.nextLine());
		return msgAnalyzer.detectConfirmation(userAnswer);
	}

}
