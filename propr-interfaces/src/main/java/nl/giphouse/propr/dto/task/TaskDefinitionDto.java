package nl.giphouse.propr.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haye.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class TaskDefinitionDto
{
	private String name;

	private String description;

	private TaskRepetitionType periodType;

	private TaskWeight weight;

	private int frequency;
}
