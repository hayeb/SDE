package nl.giphouse.propr.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author haye
 */
@AllArgsConstructor
@Builder
@Getter
public class TaskDto
{
	private Long taskId;

	private String name;

	private String description;

	private Long assigneeId;

	private Long groupId;

	private String dueDate;

	private TaskWeight weight;

	private String completionNotes;

	private String completionDate;

	private boolean overdue;
}
