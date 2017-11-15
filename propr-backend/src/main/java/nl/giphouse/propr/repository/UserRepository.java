package nl.giphouse.propr.repository;

import nl.giphouse.propr.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author haye.
 */
public interface UserRepository extends JpaRepository<User, Long>
{
	User findOneByUsername(String username);
}
