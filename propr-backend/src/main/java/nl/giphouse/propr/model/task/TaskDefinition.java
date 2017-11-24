package nl.giphouse.propr.model.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskWeight;
import nl.giphouse.propr.model.group.Group;

/**
 * The definition of a scheduled task. The task should be done {@code frequency} times per {@code period}.
 * 
 * @author haye.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "task_definition")
public class TaskDefinition
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String name;

	@Column
	private String description;

	@ManyToOne
	@JoinColumn(name = "groep")
	private Group group;

	@Enumerated(EnumType.STRING)
	@Column
	private TaskWeight weight;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TaskRepetitionType periodType;

	@Column(nullable = false)
	private int frequency;
}
