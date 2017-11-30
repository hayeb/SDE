package nl.giphouse.propr;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskWeight;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.CompletedTask;
import nl.giphouse.propr.model.task.TaskDefinition;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import nl.giphouse.propr.repository.TaskRepository;
import nl.giphouse.propr.service.SchedulingResult;
import nl.giphouse.propr.service.SchedulingServiceImpl;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
@DataJpaTest(showSql = false)
public class SchedulingServiceImplTest
{
	@Inject
	private TestEntityManager testEntityManager;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private TaskDefinitionRepository taskDefinitionRepository;

	@Inject
	private ProprTestHelper proprTestHelper;

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

	@Test
	public void test_task_stack_generation()
	{
		final TaskDefinition def1 = proprTestHelper.testDefinition(null, "name", TaskRepetitionType.WEEK, TaskWeight.LIGHT, 4);
		final TaskDefinition def2 = proprTestHelper.testDefinition(null, "name", TaskRepetitionType.WEEK, TaskWeight.LIGHT, 1);
		final TaskDefinition def3 = proprTestHelper.testDefinition(null, "name", TaskRepetitionType.MONTH, TaskWeight.LIGHT, 2);
		final TaskDefinition def4 = proprTestHelper.testDefinition(null, "name", TaskRepetitionType.MONTH, TaskWeight.LIGHT, 4);

		// A task four times a week should generate 4 tasks in a single week.
		final List<AssignedTask> tasks1 = schedulingService.getTaskStack(Collections.singletonList(def1), LocalDate.now(),
			LocalDate.now().plusWeeks(1));
		assertEquals("def1 in 1 week", 4, tasks1.size());

		// A task once a week would generate four tasks in 4 weeks.
		final List<AssignedTask> tasks2 = schedulingService.getTaskStack(Collections.singletonList(def2), LocalDate.now(),
			LocalDate.now().plusWeeks(4));
		assertEquals("def2 in 4 weeks", 4, tasks2.size());

		// A task once a week and a task four times a week should generate 5 tasks in a week.
		final List<AssignedTask> tasks3 = schedulingService.getTaskStack(Arrays.asList(def1, def2), LocalDate.now(), LocalDate.now().plusWeeks(1));
		assertEquals("def1, def2 in 1 week", 5, tasks3.size());

		// A task twice a month should generate 2 tasks in a single month.
		final List<AssignedTask> tasks4 = schedulingService.getTaskStack(Collections.singletonList(def3), LocalDate.now(),
			LocalDate.now().plusMonths(1));
		assertEquals("def3 in 1 month", 2, tasks4.size());

		// A task four times a month should generate 4 tasks in a single month
		final List<AssignedTask> tasks5 = schedulingService.getTaskStack(Collections.singletonList(def4), LocalDate.now(),
			LocalDate.now().plusMonths(1));
		assertEquals("def4 in 1 month", 4, tasks5.size());

		// A task twice a month and a task four times a month should generate 6 tasks in a single month
		final List<AssignedTask> tasks6 = schedulingService.getTaskStack(Arrays.asList(def4, def3), LocalDate.now(), LocalDate.now().plusMonths(1));
		assertEquals("def3, def4 in 1 month", 6, tasks6.size());

		/* @formatter:off
			All tasks should generate
			Def1: 4 times a week    = 4 * 52
			Def2: 1 times a week    = 1 * 52
			Def3: 2 times a month   = 2 * 12
			Def4: 4 times a month   = 4 * 12
			-------------------------------- +
			Total:                    332 tasks
			@formatter:on
		 */
		final StopWatch sw = new StopWatch();
		sw.start();
		final List<AssignedTask> tasksTotal = schedulingService.getTaskStack(Arrays.asList(def1, def2, def3, def4), LocalDate.now(),
			LocalDate.now().plusYears(1));
		sw.stop();

		assertTrue("Stack generation <= 100ms", sw.getTime() < 100);
		assertTrue("All tasks are not yet assigned", tasksTotal.stream().allMatch(t -> t.getAssignee() == null));
		assertEquals("All tasks in a year", 332, tasksTotal.size());
	}

	/**
	 * Test the scheduling algorithm with a large amount of task definitions.
	 */
	@Test
	public void scheduling_stress_test()
	{
		final List<User> users = proprTestHelper.generateRandomUsers(100);
		users.forEach(testEntityManager::persist);

		final Group group = proprTestHelper.testGroup("groupname", users.get(0), users);
		testEntityManager.persist(group);

		final List<TaskDefinition> definitions = proprTestHelper.generateRandomTaskDefinitions(100, group, 123456123456L);
		definitions.forEach(testEntityManager::persist);

		final StopWatch sw = new StopWatch();
		sw.start();
		final SchedulingResult result = schedulingService.reschedule(group, LocalDate.now(), LocalDate.now().plusYears(1));
		sw.stop();

		log.info("Scheduling stress test took {}ms", sw.getTime());
		log.info("Scheduled {} tasks in total", result.getTasks().size());

		assertTrue("Scheduling was succesful", result.isSuccesfull());
		assertTrue("Tasks have been generated for the group", result.getTasks().size() > 0);
		assertTrue("all tasks have an assignee", result.getTasks().stream().allMatch(t -> t.getAssignee() != null));
	}

	/**
	 * A very simple scenario. There is just a single task definition and a single user.
	 */
	@Test
	public void simple_scheduling()
	{
		final User user1 = proprTestHelper.testUser("user1");
		final Group group = proprTestHelper.testGroup("group1", user1, Collections.singletonList(user1));

		testEntityManager.persist(user1);
		testEntityManager.persist(group);

		final TaskDefinition def = proprTestHelper.testDefinition(group, "def1", TaskRepetitionType.WEEK, TaskWeight.LIGHT, 2);
		testEntityManager.persist(def);

		final SchedulingResult result = schedulingService.reschedule(group, LocalDate.now(), LocalDate.now().plusMonths(1));

		assertEquals("8 tasks total", 8, result.getTasks().size());
		assertTrue("all assigned to user1", result.getTasks().stream().allMatch(t -> t.getAssignee().equals(user1)));
	}

	@Test
	public void simple_scheduling_multiple_users()
	{
		final User user1 = proprTestHelper.testUser("user1");
		final User user2 = proprTestHelper.testUser("user2");
		final User user3 = proprTestHelper.testUser("user3");
		final User user4 = proprTestHelper.testUser("user4");
		testEntityManager.persist(user1);
		testEntityManager.persist(user2);
		testEntityManager.persist(user3);
		testEntityManager.persist(user4);

		final Group group = proprTestHelper.testGroup("groupname", user1, Arrays.asList(user1, user2, user3, user4));
		testEntityManager.persist(group);

		final TaskDefinition def1 = proprTestHelper.testDefinition(group, "task1", TaskRepetitionType.WEEK, TaskWeight.LIGHT, 2);
		final TaskDefinition def2 = proprTestHelper.testDefinition(group, "task2", TaskRepetitionType.WEEK, TaskWeight.MEDIUM, 1);
		final TaskDefinition def3 = proprTestHelper.testDefinition(group, "task3", TaskRepetitionType.MONTH, TaskWeight.HEAVY, 1);
		final TaskDefinition def4 = proprTestHelper.testDefinition(group, "task4", TaskRepetitionType.MONTH, TaskWeight.HEAVY, 1);
		testEntityManager.persist(def1);
		testEntityManager.persist(def2);
		testEntityManager.persist(def3);
		testEntityManager.persist(def4);

		final StopWatch sw = new StopWatch();
		sw.start();
		final SchedulingResult result = schedulingService.reschedule(group, LocalDate.now(), LocalDate.now().plusMonths(1));
		sw.stop();
		log.info("Rescheduling case 1 took {}ms", sw.getTime());

		assertTrue("14 tasks scheduled", result.getTasks().size() == 14);
		assertTrue("All tasks are assigned", result.getTasks().stream().allMatch(t -> t.getAssignee() != null));
	}

	@Test
	public void test_task_already_done_in_schedule()
	{
		final User user1 = proprTestHelper.testUser("user1");
		final User user2 = proprTestHelper.testUser("user2");
		final User user3 = proprTestHelper.testUser("user3");
		final User user4 = proprTestHelper.testUser("user4");
		testEntityManager.persist(user1);
		testEntityManager.persist(user2);
		testEntityManager.persist(user3);
		testEntityManager.persist(user4);

		final Group group = proprTestHelper.testGroup("groupname", user1, Arrays.asList(user1, user2, user3, user4));
		testEntityManager.persist(group);

		final TaskDefinition def1 = proprTestHelper.testDefinition(group, "task1", TaskRepetitionType.WEEK, TaskWeight.LIGHT, 2);
		final TaskDefinition def2 = proprTestHelper.testDefinition(group, "task2", TaskRepetitionType.WEEK, TaskWeight.MEDIUM, 1);
		final TaskDefinition def3 = proprTestHelper.testDefinition(group, "task3", TaskRepetitionType.MONTH, TaskWeight.HEAVY, 1);
		final TaskDefinition def4 = proprTestHelper.testDefinition(group, "task4", TaskRepetitionType.MONTH, TaskWeight.HEAVY, 1);
		testEntityManager.persist(def1);
		testEntityManager.persist(def2);
		testEntityManager.persist(def3);
		testEntityManager.persist(def4);

		final AssignedTask task1 = proprTestHelper.testTask(def1, user1, LocalDate.now().plusDays(1),  new CompletedTask());
		final AssignedTask task2 = proprTestHelper.testTask(def2, user1, LocalDate.now().plusWeeks(1),  new CompletedTask());
		final AssignedTask task3 = proprTestHelper.testTask(def3, user1, LocalDate.now().plusWeeks(2),  new CompletedTask());
		final AssignedTask task4 = proprTestHelper.testTask(def4, user1, LocalDate.now().plusWeeks(3),  new CompletedTask());

		testEntityManager.persist(task1);
		testEntityManager.persist(task2);
		testEntityManager.persist(task3);
		testEntityManager.persist(task4);

		final StopWatch sw = new StopWatch();
		sw.start();
		final SchedulingResult result = schedulingService.reschedule(group, LocalDate.now(), LocalDate.now().plusMonths(1));
		sw.stop();
		log.info("Rescheduling case 1 took {}ms", sw.getTime());
		final List<AssignedTask> tasks = taskRepository.findTodoTasksInGroup(group);

		assertEquals("10 tasks scheduled", 10, tasks.size());
		assertTrue("All tasks are assigned", result.getTasks().stream().allMatch(t -> t.getAssignee() != null));
	}
}
