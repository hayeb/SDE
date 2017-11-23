package nl.giphouse.propr.service;

import nl.giphouse.propr.model.group.Group;

import java.time.LocalDate;

/**
 * @author haye.
 */
public interface ScheduleService {

	void reschedule(Group group, LocalDate date, LocalDate endDate);
}
