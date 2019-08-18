package ch.app.bot;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import ch.app.account.AccountAccessManager;
import ch.app.file.AppointmentJsonRepository;
import ch.app.file.AppointmentRepository;
import ch.app.models.Appointment;
import ch.app.models.User;

public class ChatBot {
	
	final private String[] ASSISTANCE_PROMPT = { "When did this happen?", "Where are you now?", "May we come assist you?" };
	final private Scanner INPUT = new Scanner(System.in);
	
	private User userProfile;
	private MessageAnalyzer msgAnalyzer;

	/**
	 * Get user profile and begin conversation
	 */
	public void run() {
		msgAnalyzer = new MessageAnalyzer();
		userProfile = startUp();
		greeting(userProfile.getFirstName());
		getChatTopic();
	}

	/**
	 * @return User current user profile to be used in chat
	 */
	private User startUp() {
		AccountAccessManager accountM = new AccountAccessManager();
		System.out.println("Hello, do you have an account with us? (Y/N)");
		String ans = msgAnalyzer.checkForInsult(INPUT.nextLine());
		User currentUser = new User();
		if (ans.equalsIgnoreCase("Y")) {
			currentUser = accountM.logIn();
		} else {
			System.out.println("\nWould you like to create a new profile? (Y/N)");
			ans = msgAnalyzer.checkForInsult(INPUT.nextLine());
			if (ans.equalsIgnoreCase("Y")) {
				currentUser = accountM.createProfile();
			} else {
				disconnect();
			}
		}
		return currentUser;
	}

	/**
	 * Greet the user
	 * 
	 * @param name name of user
	 */
	private void greeting(String name) {
		System.out.println("\nGreetings " + name + ", welcome to vehicle help live chat.");
	}

	/**
	 * Detect chat topic from user
	 */
	private void getChatTopic() {
		System.out.println("How may I assist you?(Ex. \"I got into an accident\", \"I need help with a rental\")");
		String message = msgAnalyzer.checkForInsult(INPUT.nextLine());
		String situation = msgAnalyzer.detectSituation(message);
		if (situation.equals("trouble")) {
			troubleSituation();
		} else if (situation.equals("rental")) {
			rentalSituation();
		} else {
			System.out.println("No problems?");
		}
		System.out.println("\nIs there anything else I can help you with? (Y/N)");
		message = msgAnalyzer.checkForInsult(INPUT.nextLine());
		if (message.equalsIgnoreCase("Y")) {
			getChatTopic();
		} else {
			disconnect();
		}
	}

	/**
	 * Detect where and when trouble happened, assist user if requested. Otherwise
	 * detect when to come assist user
	 */
	private void troubleSituation() {
		
		String userMessage = "";
		for (int i = 0; i < ASSISTANCE_PROMPT.length; i++) {
			System.out.println(ASSISTANCE_PROMPT[i]);
			userMessage = msgAnalyzer.checkForInsult(INPUT.nextLine());
			if (i == ASSISTANCE_PROMPT.length - 1) {
				if (msgAnalyzer.detectConfirmation(userMessage)) {
					System.out.println("\nWe are on our way. Hang tight.");
				} else {
					System.out.println("\nWhen should we arrive?");
					userMessage = msgAnalyzer.checkForInsult(INPUT.nextLine());
					System.out.println("\nWe will see you then.");
				}
			}
		}

	}

	/**
	 * Detect if user wants to make an appointment
	 */
	private void rentalSituation() {
		UUID userID = userProfile.getId();
		AppointmentRepository appRepo = new AppointmentJsonRepository();
		List<Appointment> userApps = appRepo.load(userID);
		AppointmentManager appManager = new AppointmentManager(userID);
		System.out.println("\nWould you like to set up an appointment?");
		String userMessage = msgAnalyzer.checkForInsult(INPUT.nextLine());
		if (msgAnalyzer.detectConfirmation(userMessage)) {
			appManager.setUpAppointment();
		} else if (userApps.size() > 0) {
			System.out.println("\nWould you like to view/Edit existing appointment?");
			userMessage = msgAnalyzer.checkForInsult(INPUT.nextLine());
			if (msgAnalyzer.detectConfirmation(userMessage)) {
				appManager.modifyAppointment();
			}
		}
	}
	
	/**
	 * Disconnect user from chat
	 */
	public void disconnect() {
		System.out.println("\nGood-Bye!");
		System.out.println("You have been disconnected from chat.");
		System.exit(0);
	}

}
