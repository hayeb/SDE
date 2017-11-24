package nl.giphouse.propr.service;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.giphouse.propr.model.task.AssignedTask;

/**
 * @author haye
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchedulingResult
{
	private boolean isSuccesfull;

	private String message;

	private List<AssignedTask> tasks;

	public static SchedulingResult invalidPeriod()
	{
		return new SchedulingResult(false, "Scheduling failed: The period has length 0", null);
	}

	public static SchedulingResult noUsersInGroup()
	{
		return new SchedulingResult(false, "Scheduling failed: No users in group", null);
	}

	public static SchedulingResult success(final List<AssignedTask> tasks)
	{
		return new SchedulingResult(true, "Scheduling succeeded", tasks);
	}
}

