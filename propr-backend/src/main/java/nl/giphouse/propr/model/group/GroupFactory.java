package nl.giphouse.propr.model.group;

import java.util.stream.Collectors;

import nl.giphouse.propr.dto.group.GroupDto;
import nl.giphouse.propr.model.user.User;

import org.springframework.stereotype.Component;

/**
 * @author haye.
 */
@Component
public class GroupFactory
{

	public GroupDto fromEntity(final Group group)
	{
		return GroupDto.builder()
			.admin(group.getAdmin().getUsername())
			.groupName(group.getName())
			.usernames(group.getUsers().stream().map(User::getUsername).collect(Collectors.toList()))
			.build();
	}
}
