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
	private long groupId;

	private String name;

	private String description;

	// TODO change these
	private TaskRepetitionType periodType;
	//private String periodType;

	private TaskWeight weight;
	//private String taskWeight;

	private int frequency;
}
