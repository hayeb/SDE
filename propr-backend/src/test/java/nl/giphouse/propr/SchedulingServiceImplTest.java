package nl.giphouse.propr;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

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

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author haye.
 */
@Slf4j
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

	@Before
	public void setup()
	{
		schedulingService = new SchedulingServiceImpl(taskRepository, taskDefinitionRepository);
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

	/**
	 * A very simple scenario. There is just a single task definition and a single user.
	 */
	@Test
	public void simple_scheduling()
	{
		final User user1 = testUser("user1");
		final Group group = testGroup("group1", user1, Collections.singletonList(user1));

		testEntityManager.persist(user1);
		testEntityManager.persist(group);

		schedulingService.reschedule(group, LocalDate.now(), LocalDate.now().plusMonths(1));

		final List<AssignedTask> tasks = taskRepository.findAllByAssigneeAndDefinitionGroup(user1, group);

		assertEquals("8 tasks total", 8, tasks.size());
		assertTrue("all assigned to user1", tasks.stream().allMatch(t -> t.getAssignee().equals(user1)));
	}

	@Test
	public void simple_scheduling_multiple_users()
	{
		final User user1 = testUser("user1");
		final User user2 = testUser("user2");
		final User user3 = testUser("user3");
		final User user4 = testUser("user4");
		testEntityManager.persist(user1);
		testEntityManager.persist(user2);
		testEntityManager.persist(user3);
		testEntityManager.persist(user4);

		final Group group = testGroup("groupname", user1, Arrays.asList(user1, user2, user3, user4));
		testEntityManager.persist(group);

		final TaskDefinition def1 = testDefinition(group, "task1", TaskRepetitionType.WEEK, TaskWeight.LIGHT, 2);
		final TaskDefinition def2 = testDefinition(group, "task2", TaskRepetitionType.WEEK, TaskWeight.MEDIUM, 1);
		final TaskDefinition def3 = testDefinition(group, "task3", TaskRepetitionType.MONTH, TaskWeight.HEAVY, 1);
		final TaskDefinition def4 = testDefinition(group, "task4", TaskRepetitionType.MONTH, TaskWeight.HEAVY, 1);
		testEntityManager.persist(def1);
		testEntityManager.persist(def2);
		testEntityManager.persist(def3);
		testEntityManager.persist(def4);

		final StopWatch sw = new StopWatch();
		sw.start();
		schedulingService.reschedule(group, LocalDate.now(), LocalDate.now().plusMonths(1));
		sw.stop();
		log.info("Rescheduling case 1 took {}ms", sw.getTime());
		final List<AssignedTask> tasks = taskRepository.findAllByDefinitionGroupAndStatusIn(group, Collections.singletonList(TaskStatus.TODO));
		assertTrue("14 tasks scheduled", tasks.size() == 14);
	}

	@Test
	public void test_task_already_done_in_schedule()
	{
		final User user1 = testUser("user1");
		final User user2 = testUser("user2");
		final User user3 = testUser("user3");
		final User user4 = testUser("user4");
		testEntityManager.persist(user1);
		testEntityManager.persist(user2);
		testEntityManager.persist(user3);
		testEntityManager.persist(user4);

		final Group group = testGroup("groupname", user1, Arrays.asList(user1, user2, user3, user4));
		testEntityManager.persist(group);

		final TaskDefinition def1 = testDefinition(group, "task1", TaskRepetitionType.WEEK, TaskWeight.LIGHT, 2);
		final TaskDefinition def2 = testDefinition(group, "task2", TaskRepetitionType.WEEK, TaskWeight.MEDIUM, 1);
		final TaskDefinition def3 = testDefinition(group, "task3", TaskRepetitionType.MONTH, TaskWeight.HEAVY, 1);
		final TaskDefinition def4 = testDefinition(group, "task4", TaskRepetitionType.MONTH, TaskWeight.HEAVY, 1);
		testEntityManager.persist(def1);
		testEntityManager.persist(def2);
		testEntityManager.persist(def3);
		testEntityManager.persist(def4);

		final AssignedTask task1 = testTask(def1, user1, LocalDate.now().plusDays(1), TaskStatus.DONE);
		final AssignedTask task2 = testTask(def2, user1, LocalDate.now().plusWeeks(1), TaskStatus.DONE);
		final AssignedTask task3 = testTask(def3, user1, LocalDate.now().plusWeeks(2), TaskStatus.DONE);
		final AssignedTask task4 = testTask(def4, user1, LocalDate.now().plusWeeks(3), TaskStatus.DONE);

		testEntityManager.persist(task1);
		testEntityManager.persist(task2);
		testEntityManager.persist(task3);
		testEntityManager.persist(task4);

		final StopWatch sw = new StopWatch();
		sw.start();
		schedulingService.reschedule(group, LocalDate.now(), LocalDate.now().plusMonths(1));
		sw.stop();
		log.info("Rescheduling case 1 took {}ms", sw.getTime());
		final List<AssignedTask> tasks = taskRepository.findAllByDefinitionGroupAndStatusIn(group, Collections.singletonList(TaskStatus.TODO));
		assertEquals("10 tasks scheduled", 10, tasks.size());
	}


	private User testUser(final String name)
	{
		final User user1 = new User();
		user1.setEmail("name@test.nl");
		user1.setFirstname("name");
		user1.setLastname("van der name");
		user1.setPassword("namenamename");
		user1.setUsername(name);

		return user1;
	}

	private Group testGroup(final String name, final User admin, final List<User> users)
	{
		final Group group = new Group();
		group.setName(name);
		group.setInviteCode("invitecode");
		group.setAdmin(admin);
		group.setUsers(users);

		return group;
	}

	private TaskDefinition testDefinition(final Group group, final String name, final TaskRepetitionType type, final TaskWeight weight,
		final int freq)
	{
		final TaskDefinition taskDefinition = new TaskDefinition();
		taskDefinition.setWeight(weight);
		taskDefinition.setPeriodType(type);
		taskDefinition.setName(name);
		taskDefinition.setGroup(group);
		taskDefinition.setFrequency(freq);

		return taskDefinition;
	}

	private AssignedTask testTask(final TaskDefinition definition, final User assignee, final LocalDate dueDate, final TaskStatus taskStatus)
	{
		final AssignedTask task = new AssignedTask();
		task.setDefinition(definition);
		task.setAssignee(assignee);
		task.setDueDate(dueDate);
		task.setStatus(taskStatus);
		return task;
	}

}
