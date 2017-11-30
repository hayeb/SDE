package nl.giphouse.propr;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskWeight;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.CompletedTask;
import nl.giphouse.propr.model.task.TaskDefinition;
import nl.giphouse.propr.model.user.User;

import org.springframework.stereotype.Component;

/**
 * @author haye
 */
@Slf4j
@Component
public class ProprTestHelper
{

	private static final List<TaskRepetitionType> types = Collections.unmodifiableList(Arrays.asList(TaskRepetitionType.values()));

	private static final List<TaskWeight> weights = Collections.unmodifiableList(Arrays.asList(TaskWeight.values()));

	public List<User> generateRandomUsers(final int n)
	{
		return IntStream.range(0, n)
			.mapToObj(number -> testUser("testUser" + number))
			.collect(Collectors.toList());
	}

	public List<TaskDefinition> generateRandomTaskDefinitions(final int n, final Group group, final Long seed)
	{
		final Random random = new Random();

		// Set a seed so that we get repeatable tests.
		random.setSeed(seed);
		return IntStream.range(0, n)
			.mapToObj(number -> testDefinition(group, "testUser" + number, randomType(random), randomWeight(random), random.nextInt(10) + 1))
			.collect(Collectors.toList());
	}

	public TaskRepetitionType randomType(final Random random)
	{
		return types.get(random.nextInt(types.size()));
	}

	public TaskWeight randomWeight(final Random random)
	{
		return weights.get(random.nextInt(weights.size()));
	}

	public User testUser(final String name)
	{
		final User user1 = new User();
		user1.setEmail("name@test.nl");
		user1.setFirstname("name");
		user1.setLastname("van der name");
		user1.setPassword("namenamename");
		user1.setUsername(name);

		return user1;
	}

	public Group testGroup(final String name, final User admin, final List<User> users)
	{
		final Group group = new Group();
		group.setName(name);
		group.setInviteCode("invitecode");
		group.setAdmin(admin);
		group.setUsers(users);

		return group;
	}

	public TaskDefinition testDefinition(final Group group, final String name, final TaskRepetitionType type, final TaskWeight weight,
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

	public AssignedTask testTask(final TaskDefinition definition, final User assignee, final LocalDate dueDate, final CompletedTask completedTask)
	{
		final AssignedTask task = new AssignedTask();
		task.setDefinition(definition);
		task.setAssignee(assignee);
		task.setDueDate(dueDate);
		task.setCompletedTask(completedTask);
		return task;
	}
}
