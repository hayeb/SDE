package nl.giphouse.propr.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author haye.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TaskRatingDto {

	private Long userId;

	private int score;

	private String comment;
}
