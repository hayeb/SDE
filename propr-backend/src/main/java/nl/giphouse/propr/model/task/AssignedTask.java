package nl.giphouse.propr.model.task;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "assigned_task")
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

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "completed_task")
	private CompletedTask completedTask;
}
