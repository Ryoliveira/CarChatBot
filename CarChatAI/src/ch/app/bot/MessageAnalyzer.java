package ch.app.bot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ch.app.file.WordRepository;
import ch.app.file.WordXlsRepository;


public class MessageAnalyzer {

	private enum Severity {
		MILD, MODERATE, SEVERE
	}
	
	private Scanner scanner;
	
	public MessageAnalyzer() {
		scanner = new Scanner(System.in);
	}

	/**
	 * @param message User provided String
	 * @return words Array words in String
	 */
	private List<String> splitMessage(String message) {
		return Arrays.asList(message.replaceAll("[^a-zA-Z]", " ").split("\\s+"));
	}

	/**
	 * If insult is detected, warn user and prompt for new message
	 * 
	 * @param message User provided String
	 * @return message String without any insults
	 */
	public String removeInsults(String message) {
		int severity = getSeverity(message);
		if (severity == Severity.valueOf("MILD").ordinal() || severity == Severity.valueOf("MODERATE").ordinal()) {
			message = insultWarning();
		} else if (severity == Severity.valueOf("SEVERE").ordinal()) {
			handleSevereCase();
		}
		return message;
	}

	/**
	 * @param message User provided String
	 * @return type of situation that is detected
	 */
	public String getSituation(String message) {
		List<String> words = splitMessage(message);
		for (String word : words) {
			if (Constants.TROUBLE_KW.contains(word)) {
				return "trouble";
			} else if (Constants.RENTAL_KW.contains(word)) {
				return "rental";
			}
		}
		return "no trouble";
	}

	/**
	 * @param message User provided String
	 * @return true of confirmation word is detected
	 */
	public boolean detectConfirmation(String message) {
		List<String> words = splitMessage(message);
		for (String word : words) {
			if (Constants.CONFIRM_KW.contains(word.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param message
	 * @return severity level of insult severity
	 */
	private int getSeverity(String message) {
		WordRepository wordRepo = new WordXlsRepository();
		List<String> words = splitMessage(message);
		Map<String, Integer> wordList = wordRepo.loadFile();
		int severity = -1;
		for (String word : words) {
			if (wordList.containsKey(word)) {
				int tempSeverity = wordList.get(word);
				severity = tempSeverity > severity ? tempSeverity : severity;
			}
		}
		return severity;
	}

	/**
	 * Gives user minor warning then gets new input
	 * 
	 * @return message without insult
	 */
	private String insultWarning() {
		System.out.println("please refrain from using such language. Thank you.");
		return removeInsults(scanner.nextLine());
	}

	/**
	 * Disconnects user from chat while delivering exit message.
	 */
	private void handleSevereCase() {
		System.out.println("This type of language will not be tolerated. Terminating chat!");
		System.out.println("you have been disconnected from chat.");
		System.exit(0);
	}

}
