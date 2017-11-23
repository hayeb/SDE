package nl.giphouse.propr;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskStatus;
import nl.giphouse.propr.dto.task.TaskWeight;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.TaskDefinition;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import nl.giphouse.propr.repository.TaskRepository;
import nl.giphouse.propr.service.SchedulingServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author haye.
 */
@RunWith(SpringRunner.class)
@ComponentScan({ "nl.giphouse.propr" })
@DataJpaTest
@SpringBootTest
public class SchedulingServiceImplTest
{

	@Inject
	private TestEntityManager testEntityManager;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private TaskDefinitionRepository taskDefinitionRepository;

	private SchedulingServiceImpl schedulingService;

	private User user1;

	private User user2;

	private User user3;

	private User user4;

	private Group group;

	@Before
	public void setup()
	{
		schedulingService = new SchedulingServiceImpl(taskRepository, taskDefinitionRepository);

		user1 = new User();
		user1.setEmail("test@test.nl");
		user1.setFirstname("test");
		user1.setLastname("van der testen");
		user1.setPassword("testtesttest");
		user1.setUsername("user1");

		user2 = new User();
		user2.setEmail("test@test.nl");
		user2.setFirstname("test");
		user2.setLastname("van der testen");
		user2.setPassword("testtesttest");
		user2.setUsername("user2");

		user3 = new User();
		user3.setEmail("test@test.nl");
		user3.setFirstname("test");
		user3.setLastname("van der testen");
		user3.setPassword("testtesttest");
		user3.setUsername("user3");

		user4 = new User();
		user4.setEmail("test@test.nl");
		user4.setFirstname("test");
		user4.setLastname("van der testen");
		user4.setPassword("testtesttest");
		user4.setUsername("user4");

		group = new Group();
		group.setUsers(Arrays.asList(user1, user2, user3, user4));
		group.setAdmin(user1);
		group.setName("group1");

		testEntityManager.persist(user1);
		testEntityManager.persist(user2);
		testEntityManager.persist(user3);
		testEntityManager.persist(user4);
		testEntityManager.persist(group);

		final TaskDefinition d1 = new TaskDefinition();
		d1.setWeight(TaskWeight.HEAVY);
		d1.setPeriodType(TaskRepetitionType.WEEK);
		d1.setName("d1");
		d1.setGroup(group);

		final TaskDefinition d2 = new TaskDefinition();
		d2.setWeight(TaskWeight.MEDIUM);
		d2.setPeriodType(TaskRepetitionType.WEEK);
		d2.setName("d2");
		d2.setGroup(group);

		final TaskDefinition d3 = new TaskDefinition();
		d3.setWeight(TaskWeight.LIGHT);
		d3.setPeriodType(TaskRepetitionType.WEEK);
		d3.setName("d3");
		d3.setGroup(group);
		testEntityManager.persist(d1);
		testEntityManager.persist(d2);
		testEntityManager.persist(d3);

		final AssignedTask t1 = new AssignedTask();
		t1.setDefinition(d1);
		t1.setAssignee(user1);
		t1.setStatus(TaskStatus.OVERDUE);
		t1.setDueDate(LocalDate.now().minusDays(1));

		final AssignedTask t2 = new AssignedTask();
		t2.setDefinition(d2);
		t2.setAssignee(user1);
		t2.setStatus(TaskStatus.DONE);
		t2.setDueDate(LocalDate.now());

		final AssignedTask t3 = new AssignedTask();
		t3.setDefinition(d3);
		t3.setAssignee(user1);
		t3.setStatus(TaskStatus.TODO);
		t3.setDueDate(LocalDate.now().plusDays(1));

		testEntityManager.persist(t1);
		testEntityManager.persist(t2);
		testEntityManager.persist(t3);
	}

	@Test
	public void simpleTest()
	{
		schedulingService.reschedule(group, LocalDate.now(), 15);
	}

	@Test
	public void test_get_period_blocks()
	{
		final List<LocalDate> twoWeek = schedulingService.getPeriodBlocks(LocalDate.now(), LocalDate.now().plusWeeks(2), TaskRepetitionType.WEEK);
		assertEquals("Two weeks", 2, twoWeek.size());

		final List<LocalDate> sixWeeks = schedulingService.getPeriodBlocks(LocalDate.now(), LocalDate.now().plusWeeks(6).minusDays(1),
			TaskRepetitionType.WEEK);
		assertEquals("Six weeks", 5, sixWeeks.size());

		final List<LocalDate> zeroWeeks = schedulingService.getPeriodBlocks(LocalDate.now(), LocalDate.now().plusWeeks(1).minusDays(1),
			TaskRepetitionType.WEEK);
		assertEquals("Zero weeks", 0, zeroWeeks.size());

		final List<LocalDate> sixMonths = schedulingService.getPeriodBlocks(LocalDate.now(), LocalDate.now().plusMonths(6), TaskRepetitionType.MONTH);
		assertEquals("Six months", 6, sixMonths.size());
	}
}
