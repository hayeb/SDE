package nl.giphouse.propr.service;

import java.time.LocalDate;

import nl.giphouse.propr.model.group.Group;

/**
 * @author haye.
 */
public interface ScheduleService
{
	SchedulingResult reschedule(Group group, LocalDate date, LocalDate endDate);
}
