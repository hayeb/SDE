package nl.giphouse.propr.service;

import nl.giphouse.propr.model.group.Group;

import java.time.LocalDate;

/**
 * @author haye.
 */
public interface ScheduleService {

	void reschedule(final Group group, final LocalDate date, final int days);
}
