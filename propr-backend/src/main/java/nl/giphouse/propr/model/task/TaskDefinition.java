package nl.giphouse.propr.model.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.giphouse.propr.dto.task.PeriodType;
import nl.giphouse.propr.dto.task.TaskWeight;
import nl.giphouse.propr.model.group.Group;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The definition of a scheduled task. The task should be done {@code frequency} times per {@code period}.
 * @author haye.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class TaskDefinition {

	@Id
	private Long id;

	@Column
	private String name;
	
	@Column
	private String description;

	@ManyToOne
	@JoinColumn(name = "group")
	private Group group;

	@Enumerated(EnumType.STRING)
	@Column
	private TaskWeight weight;

	@Enumerated
	@Column(nullable = false)
	private PeriodType periodType;
	
	@Column(nullable = false)
	private int frequency;
}
