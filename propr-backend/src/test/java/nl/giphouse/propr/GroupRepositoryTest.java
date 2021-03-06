package nl.giphouse.propr;

import java.util.Collections;

import javax.inject.Inject;

import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.GroupRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author haye.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class GroupRepositoryTest
{

	@Inject
	private TestEntityManager testEntityManager;

	@Inject
	private GroupRepository groupRepository;

	@Test
	public void findGroupsByUser()
	{
		final User u1 = new User();
		u1.setUsername("user1");
		u1.setPassword("pass1");
		u1.setEmail("user1@test.nl");
		u1.setFirstname("Henk");
		u1.setLastname("van der Plas");
		final User u2 = new User();
		u2.setUsername("user2");
		u2.setPassword("pass2");
		u2.setEmail("user2@test.nl");
		u2.setFirstname("Johannes");
		u2.setLastname("Burg");
		final Group g1 = new Group("group1", "invite1", u1, Collections.singletonList(u1));

		testEntityManager.persist(u1);
		testEntityManager.persist(u2);
		testEntityManager.persist(g1);

		assertEquals("u1 has a single group", 1, groupRepository.findGroupsByUsers(u1).size());
		assertEquals("u2 has no groups", 0, groupRepository.findGroupsByUsers(u2).size());
	}
}
