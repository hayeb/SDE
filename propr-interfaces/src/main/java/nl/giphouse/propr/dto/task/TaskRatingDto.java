package nl.giphouse.propr.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author haye.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskRatingDto {

	private int score;

	private String comment;
}
