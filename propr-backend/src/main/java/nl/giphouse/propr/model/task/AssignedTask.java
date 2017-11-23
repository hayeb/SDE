package nl.giphouse.propr.model.task;

import java.io.Serializable;
import java.time.LocalDate;

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

import nl.giphouse.propr.dto.task.TaskStatus;
import nl.giphouse.propr.model.user.User;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * A task to be done in a certain period by user. Is generated when the group admin hits the "generate schedule" button.
 *
 * @author haye
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "assigned_task")
public class AssignedTask implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "assignee")
	private User assignee;

	@ManyToOne
	@JoinColumn(name = "definition")
	private TaskDefinition definition;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(nullable = false)
	private LocalDate dueDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TaskStatus status;

}
