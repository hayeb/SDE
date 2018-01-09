package nl.giphouse.propr.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye.
 */
public class TaskDefinitionInvalidException extends RuntimeException {

	public TaskDefinitionInvalidException(final String message)
	{
		super(message);
	}
}
