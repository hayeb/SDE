package nl.giphouse.propr.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye.
 */
@AllArgsConstructor
@Getter
public class GroupNotAuthorizedException extends RuntimeException {

	private final String groupName;

	private final String username;
}
