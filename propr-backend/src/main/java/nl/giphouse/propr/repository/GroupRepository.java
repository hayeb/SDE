package nl.giphouse.propr.repository;

import nl.giphouse.propr.model.Group;
import nl.giphouse.propr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author haye.
 */
public interface GroupRepository extends JpaRepository<Group, Long> {

	List<Group> findGroupsByAdmin_Id(final User admin);

	List<Group> findGroupsByUsers(final User user);
}
