package nl.giphouse.propr.model.task;

import java.time.format.DateTimeFormatter;

import nl.giphouse.propr.dto.task.TaskDefinitionDto;
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
			.taskId(task.getId())
			.name(task.getDefinition().getName())
			.description(task.getDefinition().getDescription())
			.assigneeId(task.getAssignee().getId())
			.groupId(task.getAssignee().getId())
			.dueDate(task.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
			.weight(task.getDefinition().getWeight())
			.status(task.getStatus())
			.build();
	}

	public TaskDefinitionDto fromEntity(final TaskDefinition definition)
	{
		return TaskDefinitionDto.builder()
			.name(definition.getName())
			.description(definition.getDescription())
			.weight(definition.getWeight())
			.periodType(definition.getPeriodType())
			.frequency(definition.getFrequency())
			.build();
	}
}
