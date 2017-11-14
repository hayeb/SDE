package nl.giphouse.propr.model.task;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.user.User;

/**
 * A task to be done in a certain period by user. Is generated when the group admin hits the "generate schedule" button.
 *
 * @author haye
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "task")
public class Task implements Serializable
{
	@Id
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@ManyToOne
	@JoinColumn(name = "assignee")
	private User assignee;

	@ManyToOne
	@JoinColumn(name = "group")
	private Group group;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(nullable = false)
	private LocalDate dueDate;
}
