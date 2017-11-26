package nl.giphouse.propr.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haye.
 */
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TaskCompletionDto {

	final String taskCompletionDescription;

	final byte[] taskComppletionImage;
}
