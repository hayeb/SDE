package nl.giphouse.propr.model.task;

import nl.giphouse.propr.dto.task.TaskDto;
import org.springframework.stereotype.Component;

/**
 * @author haye.
 */
@Component
public class TaskFactory {

	public TaskDto fromEntity(final Task task)
	{
		return TaskDto.builder()
			.name(task.getName())
			.description(task.getDescription())
			.assigneeId(task.getAssignee().getId())
			.groupId(task.getAssignee().getId())
			.dueDate(task.getDueDate())
			.build();
	}
}
