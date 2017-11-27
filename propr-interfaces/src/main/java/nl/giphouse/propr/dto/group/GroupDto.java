package nl.giphouse.propr.dto.group;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author haye.
 */
@AllArgsConstructor
@Builder
@Getter
public class GroupDto
{
	private Long groupId;

	private String groupName;

	private String admin;

	private List<String> usernames;
}
