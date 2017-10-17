package nl.giphouse.propr;

import nl.giphouse.propr.model.Group;
import nl.giphouse.propr.model.User;
import nl.giphouse.propr.repository.GroupRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author haye.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class GroupRepositoryTest {

	@Inject
	private TestEntityManager testEntityManager;

	@Inject
	private GroupRepository groupRepository;

	@Test
	public void findGroupsByUser() {
		final User u1 = new User("user1", "pass1", "user1@test.nl", true);
		final User u2 = new User("user2", "pass2", "user2@test.nl", true);
		final Group g1 = new Group("group1", "invite1", u1, Collections.singletonList(u2));

		testEntityManager.persist(u1);
		testEntityManager.persist(u2);
		testEntityManager.persist(g1);

		assertEquals("u1 has no groups", 0, groupRepository.findGroupsByUsers(u1).size());
		assertEquals("u2 has a single group", 1, groupRepository.findGroupsByUsers(u2).size());
	}
}
