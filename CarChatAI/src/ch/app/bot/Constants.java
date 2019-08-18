package ch.app.bot;

import java.util.Arrays;
import java.util.List;

public final class Constants {
	
	private Constants() {
		
	}
	//MessageAnalyzer Key Word Constants
	public static final List<String> CONFIRM_KW = Arrays.asList("y", "yes", "yup", "ok", "fine", "okay", "yeah", "yea");
	public static final List<String> TROUBLE_KW = Arrays.asList("broken", "broke", "down", "trouble", "wont", "accident");
	public static final List<String> RENTAL_KW = Arrays.asList("shop", "repaired", "rental", "rent", "appointment");
	
	//AppointmentManager Menu Constants
	public static final List<String> EDIT_OPTIONS = Arrays.asList("Date of appointment", "Time of appointment", "Car to view");
	public static final List<String> MODIFY_OPTIONS = Arrays.asList("View Appointment(s)", "Edit Appointment(s)", "Remove Appointment(s)", "Cancel");

}
