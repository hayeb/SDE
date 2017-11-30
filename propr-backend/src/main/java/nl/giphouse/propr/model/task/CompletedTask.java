package nl.giphouse.propr.model.task;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author haye.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompletedTask
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(mappedBy = "completedTask")
	private AssignedTask task;

	@Column
	private String description;

	@Column
	private LocalDate date;

	@OneToMany(mappedBy = TaskRating.PROPERTY_COMPLETED_TASK, fetch = FetchType.LAZY)
	private List<TaskRating> ratings;

	private byte[] image;
}
