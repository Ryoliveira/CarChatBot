package ch.app.bot;

import java.util.List;
import java.util.Scanner;
import ch.app.account.AccountAccessManager;
import ch.app.file.AppointmentJsonRepository;
import ch.app.file.AppointmentRepository;
import ch.app.models.Appointment;
import ch.app.models.User;

public class ChatBot {

	private User profile;
	private MessageAnalyzer msgAnalyzer;
	private Scanner scanner;
	private AccountAccessManager accessManager;
	private AppointmentRepository appRepo;
	private RentalService rentalService;

	public ChatBot() {
		scanner = new Scanner(System.in);
		msgAnalyzer = new MessageAnalyzer();
		accessManager = new AccountAccessManager();
		appRepo = new AppointmentJsonRepository();
	}

	/**
	 * Get user profile and begin conversation
	 */
	public void runBot() {
		getUserProfile();
		greetUser();
		handleSituation();
	}

	/**
	 * Get or create user profile
	 * 
	 * @return User current user profile to be used in chat
	 */
	private void getUserProfile() {
		System.out.println("Hello, do you have an account with us? (Y/N)");
		String profileAnswer = msgAnalyzer.removeInsults(scanner.nextLine());
		User user = new User();
		if (profileAnswer.equalsIgnoreCase("Y")) {
			user = accessManager.accessAccount();
		} else {
			System.out.println("\nWould you like to create a new profile? (Y/N)");
			String createUserAnswer = msgAnalyzer.removeInsults(scanner.nextLine());
			if (createUserAnswer.equalsIgnoreCase("Y")) {
				user = accessManager.createProfile();
			} else {
				disconnectUser();
			}
		}
		profile = user;
		rentalService = new RentalService(profile.getId());
	}

	/**
	 * Greet the user
	 */
	private void greetUser() {
		System.out.println("\nGreetings " + profile.getFirstName() + ", welcome to vehicle help live chat.");
	}

	/**
	 * Detect chat topic from user
	 */
	private void handleSituation() {
		System.out.println("How may I assist you?(Ex. \"I got into an accident\", \"I need help with a rental\")");
		String message = msgAnalyzer.removeInsults(scanner.nextLine());
		String situation = msgAnalyzer.getSituation(message);
		switch (situation) {
			case "trouble":
				handleTroubleSituation();
				break;
			case "rental":
				handleRentalSituation();
				break;
			default:
				System.out.println("No problems?");
		}
		System.out.println("\nIs there anything else I can help you with? (Y/N)");
		message = msgAnalyzer.removeInsults(scanner.nextLine());
		if (message.equalsIgnoreCase("Y")) {
			handleSituation();
		} else {
			disconnectUser();
		}
	}

	/**
	 * Detect where and when trouble happened, assist user if requested. Otherwise
	 * detect when to come assist user
	 */
	private void handleTroubleSituation() {
		String userMessage = "";
		for (int i = 0; i < Constants.ASSISTANCE_PROMPT.length; i++) {
			System.out.println(Constants.ASSISTANCE_PROMPT[i]);
			userMessage = msgAnalyzer.removeInsults(scanner.nextLine());
			if (i == Constants.ASSISTANCE_PROMPT.length - 1) {
				if (msgAnalyzer.detectConfirmation(userMessage)) {
					System.out.println("\nWe are on our way. Hang tight.");
				} else {
					System.out.println("\nWhen should we arrive?");
					userMessage = msgAnalyzer.removeInsults(scanner.nextLine());
					System.out.println("\nWe will see you then.");
				}
			}
		}

	}

	/**
	 * Detect if user wants to make an appointment
	 */
	private void handleRentalSituation() {
		List<Appointment> userApps = appRepo.loadUser(profile.getId());
		System.out.println("\nWould you like to set up an appointment?");
		String userMessage = msgAnalyzer.removeInsults(scanner.nextLine());
		if (msgAnalyzer.detectConfirmation(userMessage)) {
			rentalService.setUpAppointment();
		} else if (userApps.size() > 0) {
			System.out.println("\nWould you like to view/Edit existing appointment?");
			userMessage = msgAnalyzer.removeInsults(scanner.nextLine());
			if (msgAnalyzer.detectConfirmation(userMessage)) {
				rentalService.selectAppointmentAction();
			}
		}
	}

	/**
	 * Disconnect user from chat
	 */
	public void disconnectUser() {
		System.out.println("\nGood-Bye!");
		System.out.println("You have been disconnected from chat.");
		System.exit(0);
	}

}
