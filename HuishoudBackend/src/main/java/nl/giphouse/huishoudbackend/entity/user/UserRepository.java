package nl.giphouse.huishoudbackend.entity.user;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author haye.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

}
