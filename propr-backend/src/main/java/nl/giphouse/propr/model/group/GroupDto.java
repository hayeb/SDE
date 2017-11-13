package nl.giphouse.propr.model.group;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Builder;
import nl.giphouse.propr.model.user.User;

/**
 * @author haye.
 */
@AllArgsConstructor
@Builder
@Getter
public class GroupDto {

	private String groupName;

	private String admin;

	private List<String> usernames;

	public static GroupDto fromGroup(final Group group)
	{
		return GroupDto.builder()
			.groupName(group.getName())
			.admin(group.getAdmin().getUsername())
			.usernames(group.getUsers().stream().map(User::getUsername).collect(Collectors.toList()))
			.build();
	}

}
