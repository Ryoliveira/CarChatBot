package ch.app.bot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ch.app.file.WordRepository;
import ch.app.file.WordXlsRepository;



public class MessageAnalyzer {
	
	final private Scanner input = new Scanner(System.in);
	final private List<String> confirmKW = Arrays.asList("yes", "yup", "ok", "fine", "okay", "yeah", "yea");
	final private List<String> troubleKW = Arrays.asList("broken", "broke", "down", "trouble", "wont");
	final private  List<String> rentalKW =  Arrays.asList("shop", "repaired", "rental", "rent", "appointment");
	
	private enum Severity{
		MILD, MODERATE, SEVERE
	}

	/**
	 * @param message User provided String
	 * @return words Array words in String
	 */
	private List<String> splitMessage(String message) {
		return Arrays.asList(message.replaceAll("[^a-zA-Z]", " ").split("\\s+"));
	}

	/**
	 * @param message User provided String
	 * @return message String without any insults
	 */
	public String checkForInsult(String message) {
		int severity = getSeverity(message);
		if (severity == Severity.valueOf("MILD").ordinal() || severity == Severity.valueOf("MODERATE").ordinal()) {
			message = insultWarning();
		} else if(severity == Severity.valueOf("SEVERE").ordinal()){
			severeCase();
		}
		return message;
	}

	/**
	 * @param message User provided String
	 * @return type of situation that is detected
	 */
	public String detectSituation(String message) {
		List<String> words = splitMessage(message);
		for (String word : words) {
			if (troubleKW.contains(word)) {
				return "trouble";
			} else if (rentalKW.contains(word)) {
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
			if (confirmKW.contains(word)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param message
	 * @return
	 */
	private int getSeverity(String message) {
		WordRepository wordRepo = new WordXlsRepository();
		List<String> words = splitMessage(message);
		Map<String, Integer> wordList = wordRepo.load();
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
		return checkForInsult(input.nextLine());
	}
	
	/**
	 * Disconnects user from chat while delivering exit message.
	 */
	private void severeCase() {
		System.out.println("This type of language will not be tolerated. Terminating chat!");
		System.out.println("you have been disconnected from chat.");
		System.exit(0);
	}

}
