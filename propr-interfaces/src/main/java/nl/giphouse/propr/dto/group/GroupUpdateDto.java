package nl.giphouse.propr.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye
 */
@AllArgsConstructor
@Getter
public class GroupUpdateDto
{
	private final String name;

	private final String inviteCode;

	private final byte[] image;
}
