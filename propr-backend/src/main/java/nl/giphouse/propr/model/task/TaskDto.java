package nl.giphouse.propr.model.task;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;

/**
 * @author haye
 */
@AllArgsConstructor
@Builder
@Getter
public class TaskDto
{
	private String name;

	private String description;

	private Long assigneeId;

	private Long groupId;

	private LocalDate dueDate;

	public static TaskDto fromEntity(final Task task)
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
