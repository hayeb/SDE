package nl.giphouse.propr.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye
 */
@AllArgsConstructor
@Getter
public class GroupJoinDto
{
	private final String groupName;

	private final String enteredCode;
}
