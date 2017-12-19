package nl.giphouse.propr.model.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskDto;

import nl.giphouse.propr.dto.task.TaskRatingDto;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author haye.
 */
@Component
public class TaskFactory
{

	@Inject
	private TaskDefinitionRepository taskDefinitionRepository;

	@Inject
	private GroupRepository groupRepository;

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
			.completionDate(Optional.ofNullable(task.getCompletedTask())
				.map(CompletedTask::getDate)
				.map(TaskFactory::formatDate)
				.orElse(null))
			.completionNotes(Optional.ofNullable(task.getCompletedTask())
				.map(CompletedTask::getDescription)
				.orElse(null))
			.overdue(task.getCompletedTask() == null && task.getDueDate().isBefore(LocalDate.now()))
			.build();
	}

	public TaskDefinitionDto toTaskDefinitionDto(final TaskDefinition definition)
	{
		return TaskDefinitionDto.builder()
			.definitionId(definition.getId())
			.groupId(definition.getGroup().getId())
			.name(definition.getName())
			.description(definition.getDescription())
			.weight(definition.getWeight())
			.periodType(definition.getPeriodType())
			.frequency(definition.getFrequency())
			.build();
	}

	public TaskDefinition fromTaskDefitionDto(final TaskDefinitionDto dto)
	{
		final TaskDefinition taskDefinition = Optional.ofNullable(dto.getDefinitionId())
			.map(taskDefinitionRepository::findOne)
			.orElseGet(TaskDefinition::new);

		taskDefinition.setName(dto.getName());
		taskDefinition.setDescription(dto.getDescription());
		taskDefinition.setWeight(dto.getWeight());
		taskDefinition.setFrequency(dto.getFrequency());
		taskDefinition.setPeriodType(dto.getPeriodType());
		taskDefinition.setGroup(groupRepository.findOne(dto.getGroupId()));
		return taskDefinition;
	}

	public TaskRatingDto toTaskRatingDto(final TaskRating rating)
	{
		return TaskRatingDto.builder()
			.userId(rating.getAuthor().getId())
			.score(rating.getScore())
			.comment(rating.getComment())
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
