package ch.chat.message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ch.chat.file.WordRepository;
import ch.chat.file.WordXlsRepository;

public class MessageAnalyzer {
	public static Scanner input = new Scanner(System.in);
	
	/**
	 * @param message User provided String
	 * @return words Array words in String
	 */
	public static String[] splitMessage(String message) {
		String[] words = message.replaceAll("[^a-zA-Z]", " ").split("\\s+");
		return words;
	}

	/**
	 * @param message User provided String
	 * @return message String without any insults
	 */
	public static String checkForInsult(String message) {
		String[] words = splitMessage(message);
		WordRepository wordRepo = new WordXlsRepository();
		Map<String, Integer> wordList = wordRepo.load();
		int severity = 0;
		for (String word : words) {
			if (wordList.containsKey(word)) {
				int tempSeverity = wordList.get(word);
				severity = tempSeverity > severity ? tempSeverity : severity;
			}
		}
		if (severity == 1 || severity == 2) {
			System.out.println("please refrain from using such language. Thank you.");
			message = checkForInsult(input.nextLine());
		} else if(severity == 3){
			System.out.println("This type of language will not be tolerated. Terminating chat!");
			System.out.println("you have been disconnected from chat.");
			System.exit(0);
		}
		return message;
	}

	/**
	 * @param message User provided String
	 * @return type of situation that is detected
	 */
	public static String detectSituation(String message) {
		List<String> troubleWords = Arrays.asList("broken", "broke", "down", "trouble", "wont");
		List<String> rentalWords = Arrays.asList("shop", "repaired", "rental", "rent");
		String[] words = splitMessage(message);
		for (String word : words) {
			if (troubleWords.contains(word)) {
				return "trouble";
			} else if (rentalWords.contains(word)) {
				return "rental";
			}
		}
		return "no trouble";
	}

	/**
	 * @param message User provided String
	 * @return true of confirmation word is detected
	 */
	public static boolean detectConfirmation(String message) {
		String[] words = splitMessage(message);
		List<String> confirmationWords = Arrays.asList("yes", "yup", "ok", "fine", "okay", "yeah", "yea");
		for (String word : words) {
			if (confirmationWords.contains(word)) {
				return true;
			}
		}
		return false;
	}

}
