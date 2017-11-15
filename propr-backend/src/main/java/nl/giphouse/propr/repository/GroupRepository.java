package nl.giphouse.propr.repository;

import java.util.List;

import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author haye.
 */
public interface GroupRepository extends JpaRepository<Group, Long> {


	List<Group> findGroupsByUsers(final User user);

	int countByName(final String name);

	Group findGroupByName(final String name);

	List<Group> findGroupsByNameIsContaining(final String name);
}
