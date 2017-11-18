package nl.giphouse.propr.model.task;

import java.time.format.DateTimeFormatter;

import nl.giphouse.propr.dto.task.TaskDto;

import org.springframework.stereotype.Component;

/**
 * @author haye.
 */
@Component
public class TaskFactory
{
	public TaskDto fromEntity(final AssignedTask task)
	{
		return TaskDto.builder()
			.name(task.getDefinition().getName())
			.description(task.getDefinition().getDescription())
			.assigneeId(task.getAssignee().getId())
			.groupId(task.getAssignee().getId())
			.dueDate(task.getDueDate().format(DateTimeFormatter.ofPattern("d-M-yyyy")))
			.weight(task.getDefinition().getWeight())
			.status(task.getStatus())
			.build();
	}
}
