package nl.giphouse.propr.model.task;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.giphouse.propr.model.user.User;

/**
 * @author haye
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class TaskRating
{
	public final static String PROPERTY_COMPLETED_TASK = "completedTask";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "completed_task")
	private CompletedTask completedTask;

	@ManyToOne
	@JoinColumn(name = "author")
	private User author;

	private int score;

	private String comment;
}
