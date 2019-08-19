package ch.app.file;

import java.util.List;
import java.util.UUID;

import ch.app.models.Appointment;

public interface AppointmentRepository {
	void save(Appointment app);

	void update(UUID userID, int index, Appointment app);

	void remove(UUID userId, int index);

	List<Appointment> loadUser(UUID creatorId);

	List<Appointment> loadAllAppointments();
}
