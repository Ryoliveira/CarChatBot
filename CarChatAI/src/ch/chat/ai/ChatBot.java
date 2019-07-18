package ch.chat.ai;

import java.util.Scanner;

import ch.chat.account.AccountAccessManager;
import ch.chat.appointment.AppointmentManager;
import ch.chat.message.MessageAnalyzer;
import ch.chat.user.User;

public class ChatBot {
	final private Scanner input = new Scanner(System.in);
	private User userProfile;

	/**
	 * Get user profile and begin conversation
	 */
	public void run() {
		userProfile = startUp();
		greeting(userProfile.getFirstName());
		getChatTopic();
	}

	/**
	 * @return User current user profile to be used in chat
	 */
	public User startUp() {
		System.out.println("Hello, do you have an account with us? (Y/N)");
		String ans = MessageAnalyzer.checkForInsult(input.nextLine());
		User currentUser = new User();
		if (ans.equalsIgnoreCase("Y")) {
			currentUser = AccountAccessManager.logIn();
		} else {
			System.out.println("Would you like to create a new profile? (Y/N)");
			ans = MessageAnalyzer.checkForInsult(input.nextLine());
			if (ans.equalsIgnoreCase("Y")) {
				currentUser = AccountAccessManager.createProfile();
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
	public void greeting(String name) {
		System.out.println("Greetings " + name + ", welcome to vehicle help live chat." + "\nHow may I assist you?");
	}

	/**
	 * Detect chat topic from user
	 */
	public void getChatTopic() {
		String message = MessageAnalyzer.checkForInsult(input.nextLine());
		String situation = MessageAnalyzer.detectSituation(message);
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
	public void troubleSituation() {
		String[] chatMessages = { "When did this happen?", "Where are you now?", "May we come assist you?" };
		String userMessage = "";
		for (int i = 0; i < chatMessages.length; i++) {
			System.out.println(chatMessages[i]);
			userMessage = MessageAnalyzer.checkForInsult(input.nextLine());
			if (i == chatMessages.length - 1) {
				if (MessageAnalyzer.detectConfirmation(userMessage)) {
					System.out.println("We are on our way. Hang tight.");
				} else {
					System.out.println("When should we arrive?");
					userMessage = MessageAnalyzer.checkForInsult(input.nextLine());
					System.out.println("We will see you then.");
				}
			}
		}

	}

	/**
	 * Detect if user wants to make an appointment
	 */
	public void rentalSituation() {
		AppointmentManager appManager = new AppointmentManager();
		System.out.println("Would you like to set up an appointment?");
		String userMessage = MessageAnalyzer.checkForInsult(input.nextLine());
		if (MessageAnalyzer.detectConfirmation(userMessage)) {
			appManager.appointmentSetup(userProfile.getId());
		}

	}

}
