package nl.giphouse.propr.model.group;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye
 */
@Getter
@AllArgsConstructor
public class GroupAddDto
{
	private final String groupName;

	private final String groupCode;

	private final byte[] groupImage;
}
