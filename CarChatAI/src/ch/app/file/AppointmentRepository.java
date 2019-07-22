package ch.app.file;

import java.util.List;
import java.util.UUID;

import ch.app.models.Appointment;

public interface AppointmentRepository {
	void save(Appointment app);
	List<Appointment>load(UUID creatorId);
	void update(List<Appointment> apps);
	List<Appointment> loadAppointments();
}
