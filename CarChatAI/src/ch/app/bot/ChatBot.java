package ch.app.bot;

import java.util.Scanner;

import ch.app.account.AccountAccessManager;
import ch.app.models.User;

public class ChatBot {
	final private Scanner input = new Scanner(System.in);
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
		String ans = msgAnalyzer.checkForInsult(input.nextLine());
		User currentUser = new User();
		if (ans.equalsIgnoreCase("Y")) {
			currentUser = accountM.logIn();
		} else {
			System.out.println("Would you like to create a new profile? (Y/N)");
			ans = msgAnalyzer.checkForInsult(input.nextLine());
			if (ans.equalsIgnoreCase("Y")) {
				currentUser = accountM.createProfile();
			}else {
				System.out.println("Good-Bye!");
				System.exit(0);
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
		System.out.println("Greetings " + name + ", welcome to vehicle help live chat." + "\nHow may I assist you?");
	}

	/**
	 * Detect chat topic from user
	 */
	private void getChatTopic() {
		String message = msgAnalyzer.checkForInsult(input.nextLine());
		String situation = msgAnalyzer.detectSituation(message);
		if (situation.equals("trouble")) {
			troubleSituation();
		} else if (situation.equals("rental")) {
			rentalSituation();
		} else {
			System.out.println("No problems? I gotta go then!");
		}
	}

	/**
	 * Detect where and when trouble happened, assist user if requested. Otherwise
	 * detect when to come assist user
	 */
	private void troubleSituation() {
		String[] chatMessages = { "When did this happen?", "Where are you now?", "May we come assist you?" };
		String userMessage = "";
		for (int i = 0; i < chatMessages.length; i++) {
			System.out.println(chatMessages[i]);
			userMessage = msgAnalyzer.checkForInsult(input.nextLine());
			if (i == chatMessages.length - 1) {
				if (msgAnalyzer.detectConfirmation(userMessage)) {
					System.out.println("We are on our way. Hang tight.");
				} else {
					System.out.println("When should we arrive?");
					userMessage = msgAnalyzer.checkForInsult(input.nextLine());
					System.out.println("We will see you then.");
				}
			}
		}

	}

	/**
	 * Detect if user wants to make an appointment
	 */
	private void rentalSituation() {
		AppointmentManager appManager = new AppointmentManager();
		System.out.println("Would you like to set up an appointment?");
		String userMessage = msgAnalyzer.checkForInsult(input.nextLine());
		if (msgAnalyzer.detectConfirmation(userMessage)) {
			appManager.setUpAppointment(userProfile.getId());
		}

	}

}
