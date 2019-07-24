package ch.app.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.app.models.Appointment;

public class AppointmentJsonRepository implements AppointmentRepository {

	final private String FILE_NAME = "./Appointments.json";
	final private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Save new appointment
	 * 
	 * @param app new appointment created by user
	 */
	@Override
	public void save(Appointment app) {
		File file = new File(FILE_NAME);
		boolean foundProfile = false;
		Map<UUID, List<Appointment>> profiles = new HashMap<>();
		try {
			if (file.exists()) {
				profiles = getProfiles();
				if (profiles.containsKey(app.getCreatorId())) {
					profiles.get(app.getCreatorId()).add(app);
					foundProfile = true;
				}
			}
			if (!foundProfile) {
				List<Appointment> newList = new LinkedList<>();
				newList.add(app);
				profiles.put(app.getCreatorId(), newList);
			}
			mapper.writeValue(file, profiles);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return List<Appointment> list of specific user appointments
	 */
	@Override
	public List<Appointment> load(UUID userID) {
		HashMap<UUID, List<Appointment>> profiles = new HashMap<>();
		File file = new File(FILE_NAME);
		if (file.exists()) {
			profiles = getProfiles();
			if (profiles.containsKey(userID)) {
				return profiles.get(userID);
			}
		}
		// If no profile is found, original profile will be returned
		List<Appointment> emptyList = new LinkedList<>();
		return emptyList;
	}

	/**
	 * @return appointments List of appointments across all users
	 */
	@Override
	public List<Appointment> loadAppointments() {
		HashMap<UUID, List<Appointment>> profiles = getProfiles();
		List<Appointment> appointments = new LinkedList<>();
		for (Map.Entry<UUID, List<Appointment>> profile : profiles.entrySet()) {
			for (Appointment app : profile.getValue()) {
				appointments.add(app);
			}
		}
		return appointments;
	}

	/**
	 * @return profiles List of currently saved profiles
	 */
	public HashMap<UUID, List<Appointment>> getProfiles() {
		HashMap<UUID, List<Appointment>> profiles = new HashMap<>();
		File file = new File(FILE_NAME);
		if (file.exists()) {
			try {
				profiles = mapper.readValue(file, new TypeReference<HashMap<UUID, List<Appointment>>>() {
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return profiles;
	}

	@Override
	public void remove(UUID userID, int index) {
		// TODO Auto-generated method stub
		File file = new File(FILE_NAME);
		Map<UUID, List<Appointment>> profiles = new HashMap<>();
		try {
			if (file.exists()) {
				profiles = getProfiles();
				if (profiles.containsKey(userID)) {
					profiles.get(userID).remove(index);
				}
			}
			mapper.writeValue(file, profiles);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(UUID userID, int index, Appointment app) {
		// TODO Auto-generated method stub
		File file = new File(FILE_NAME);
		Map<UUID, List<Appointment>> profiles = new HashMap<>();
		try {
			if (file.exists()) {
				profiles = getProfiles();
				if (profiles.containsKey(userID)) {
					profiles.get(userID).set(index, app);
				}
			}
			mapper.writeValue(file, profiles);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
