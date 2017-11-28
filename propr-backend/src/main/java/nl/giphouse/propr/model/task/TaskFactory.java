package nl.giphouse.propr.model.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskDto;

import org.springframework.stereotype.Component;

/**
 * @author haye.
 */
@Component
public class TaskFactory
{
	public TaskDto toTaskDto(final AssignedTask task)
	{
		return TaskDto.builder()
			.taskId(task.getId())
			.name(task.getDefinition().getName())
			.description(task.getDefinition().getDescription())
			.assigneeId(task.getAssignee().getId())
			.groupId(task.getDefinition().getGroup().getId())
			.dueDate(formatDate(task.getDueDate()))
			.weight(task.getDefinition().getWeight())
			.status(task.getStatus())
			.completionDate(Optional.ofNullable(task.getCompletedTask())
				.map(CompletedTask::getDate)
				.map(TaskFactory::formatDate)
				.orElse(null))
			.completionNotes(Optional.ofNullable(task.getCompletedTask())
				.map(CompletedTask::getDescription)
				.orElse(null))
			.build();
	}

	public TaskDefinitionDto toTaskDefinitionDto(final TaskDefinition definition)
	{
		return TaskDefinitionDto.builder()
			.name(definition.getName())
			.description(definition.getDescription())
			.weight(definition.getWeight())
			.periodType(definition.getPeriodType())
			.frequency(definition.getFrequency())
			.build();
	}

	private static String formatDate(final LocalDate date)
	{
		if (date == null)
		{
			return null;
		}

		return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	}

}
