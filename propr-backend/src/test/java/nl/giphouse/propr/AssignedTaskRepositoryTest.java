package nl.giphouse.propr;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskWeight;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.CompletedTask;
import nl.giphouse.propr.model.task.TaskDefinition;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.TaskRepository;

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
 * @author haye
 */
@RunWith(SpringRunner.class)
@ComponentScan({ "nl.giphouse.propr" })
@DataJpaTest(showSql = false)
public class AssignedTaskRepositoryTest
{
	@Inject
	private TestEntityManager testEntityManager;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private ProprTestHelper proprTestHelper;

	private User user;

	private Group group;

	private TaskDefinition def;

	private AssignedTask t1;

	private AssignedTask t2;

	private AssignedTask t3;

	private AssignedTask t4;

	private CompletedTask completedTask;

	@Before
	public void setup()
	{
		user = proprTestHelper.testUser("u1");
		group = proprTestHelper.testGroup("g1", user, Collections.singletonList(user));

		testEntityManager.persist(user);
		testEntityManager.persist(group);

		def = proprTestHelper.testDefinition(group, "def1", TaskRepetitionType.WEEK, TaskWeight.MEDIUM, 1);
		testEntityManager.persist(def);

		completedTask = new CompletedTask();
		completedTask.setDate(LocalDate.now().minusDays(2));
		completedTask.setDescription("done");
		testEntityManager.persist(completedTask);

		t1 = proprTestHelper.testTask(def, user, LocalDate.now().plusDays(7), null);
		t2 = proprTestHelper.testTask(def, user, LocalDate.now().plusDays(14), null);
		t3 = proprTestHelper.testTask(def, user, LocalDate.now().minusDays(7), completedTask);
		t4 = proprTestHelper.testTask(def, user, LocalDate.now().minusDays(14), null);

		testEntityManager.persist(t1);
		testEntityManager.persist(t2);
		testEntityManager.persist(t3);
		testEntityManager.persist(t4);
	}

	@Test
	public void testTasksForUser()
	{
		final List<AssignedTask> tasks = taskRepository.findTasksForUserInGroup(group, user);
		assertEquals("Three tasks found for user", 3, tasks.size());
		assertTrue("t1 is on the list", tasks.contains(t1));
		assertTrue("t2 is on the list", tasks.contains(t2));
		assertTrue("t3 is not on the list (completed)", !tasks.contains(t3));
		assertTrue("t1 is on the list", tasks.contains(t4));
	}

	@Test
	public void groupActivity()
	{
		final List<AssignedTask> activity = taskRepository.findActivityInGroup(group);
		assertEquals("One completed, one overdue", 2, activity.size());
		assertEquals("t4 is last (overdue)", t4, activity.get(1));
		assertEquals("t3 is first (done)", t3, activity.get(0));
	}

	@Test
	public void scheduledTasks()
	{
		final List<AssignedTask> scheduled = taskRepository.findTodoTasksInGroup(group);
		assertEquals("2 tasks todo", 2, scheduled.size());
	}
}
