package nl.giphouse.propr.repository;

import java.util.List;

import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author haye.
 */
public interface GroupRepository extends JpaRepository<Group, Long>
{
	List<Group> findGroupsByUsers(final User user);

	int countByName(final String name);

	int countByInviteCode(final String inviteCode);

	Group findGroupByName(final String name);
	Group findGroupById(final long groupId);
	Group findGroupByInviteCode(final String inviteCode);
	Group findGroupNameById(final long groupId);

	List<Group> findGroupsByNameIsContaining(final String name);
}
