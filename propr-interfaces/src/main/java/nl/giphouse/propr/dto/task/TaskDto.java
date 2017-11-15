package nl.giphouse.propr.dto.task;

import java.time.LocalDate;

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
	private String name;

	private String description;

	private Long assigneeId;

	private Long groupId;

	private String dueDate;
}
