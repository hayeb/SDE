package nl.giphouse.propr.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskStatus;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.TaskDefinition;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import nl.giphouse.propr.repository.TaskRepository;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

/**
 * @author haye.
 */
@Service
@Slf4j
public class SchedulingServiceImpl implements ScheduleService
{
	private final TaskRepository taskRepository;

	private final TaskDefinitionRepository taskDefinitionRepository;

	@Inject
	public SchedulingServiceImpl(final TaskRepository taskRepository, final TaskDefinitionRepository taskDefinitionRepository)
	{
		this.taskRepository = taskRepository;
		this.taskDefinitionRepository = taskDefinitionRepository;
	}

	/**
	 * @formatter:off
	 * Reschedules all task definitions in a group for a given period. Tries to take into account:
	 * - The tasks which may have already been completed after the startdate of the new schedule. Users who have already
	 *  completed tasks in this period will get less tasks until the distribution is balanced.
	 * - The weight of tasks
	 * - Users who have since been removed from the group
	 * -
	 * @formatter:on
	 * @param group
	 *            The group for which to reschedule tasks
	 * @param startDate
	 *            The startdate of the new schedule
	 * @param endDate
	 *            The enddate of the new schedule
	 */
	public void reschedule(@NonNull final Group group, @NonNull final LocalDate startDate, @NonNull final LocalDate endDate)
	{
		// 1. Determine which tasks may have already been completed, they do not have to be planned again.
		final List<AssignedTask> doneTasksInNewSchedule = taskRepository
			.findAllByDefinitionGroupAndDueDateGreaterThanEqualAndStatusIs(group, startDate, TaskStatus.DONE);

		// 2. For each user, count the number of tasks already done which have a due date >= current date.
		final Map<User, Integer> userToDoneTasks = doneTasksInNewSchedule.stream()
			.collect(Collectors.groupingBy(AssignedTask::getAssignee,
				Collectors.summingInt(t -> t.getDefinition().getWeight().getValue())));

		// 3. Remove users from the map who have been removed from the group.
		userToDoneTasks.keySet().removeIf(user -> !group.getUsers().contains(user));

		// 4. Create a priority queue of users to record their total number of task "weight" in the new schedule
		final PriorityQueue<Pair<User, Integer>> userQueue = new PriorityQueue<>(Comparator.comparing(Pair::getRight));
		userToDoneTasks.entrySet().stream()
			.map(entry -> Pair.of(entry.getKey(), entry.getValue()))
			.forEach(userQueue::add);

		// 5. Add all users
		group.getUsers().stream()
			.filter(user -> !userToDoneTasks.containsKey(user))
			.forEach(user -> userQueue.add(Pair.of(user, 0)));

		// 6. Remove all assigned tasks in the schedule
		taskRepository.delete(taskRepository.findAllByDefinitionGroupAndDueDateGreaterThanEqualAndStatusIs(group, startDate, TaskStatus.TODO));

		// 8. Calculate the list of tasks needed to be done in ascending order of date.
		final List<AssignedTask> tasks = getTaskStack(taskDefinitionRepository.findAllByGroup(group), startDate, endDate);

		// 9. It could be that some of these tasks have already been finished.. So remove them.
		removeDoneTasks(tasks, doneTasksInNewSchedule);

		// 10. Assign each task to a user. We update the total task weight for each user, so that we can distribute the tasks evenly.
		for (final AssignedTask task : tasks)
		{
			// Pairs are immutable, so we just pop the off the queue and place them back.
			final Pair<User, Integer> userTasks = userQueue.poll();

			task.setAssignee(userTasks.getLeft());
			userQueue.add(Pair.of(userTasks.getLeft(), userTasks.getRight() + task.getDefinition().getWeight().getValue()));
		}

		// 11. Save all task definitions.
		taskRepository.save(tasks);
	}

	/**
	 * For every task in {@code doneTasksInNewSchedule}, remove the first tasks is tasks.
	 * 
	 * @param tasks
	 *            The list from which tasks are removed
	 * @param doneTasksInNewSchedule
	 *            The list with completed tasks
	 */
	private void removeDoneTasks(final @NonNull List<AssignedTask> tasks, @NonNull final List<AssignedTask> doneTasksInNewSchedule)
	{
		doneTasksInNewSchedule.sort(Comparator.comparing(AssignedTask::getDueDate));

		// Here, for every task which is already completed, we remove the first task which has the same definition.
		for (final AssignedTask task : doneTasksInNewSchedule)
		{
			IntStream.range(0, tasks.size())
				.filter(i -> tasks.get(i).getDefinition().equals(task.getDefinition()))
				.findFirst()
				.ifPresent(tasks::remove);
		}
	}

	/**
	 * Builds a "task stack" from a list of task definitions. The tasks will have due dates between {@code startDate} and {@code endDate}. The
	 * strategy for doing this is roughly the following:
	 *
	 * <ol>
	 * <li>For every definition, split the total period into blocks according to the period type (day, week, month, ...).</li>
	 * <li>For every block generated this way, start from the end and add {code freq} tasks according to the frequency in the definition. Starts from
	 * the back of the blocks, because we do not want to generate a due date on {@code startDate} if we can help it.</li>
	 * <li>At the end, sort the tasks according to their {@code dueDate}</li>
	 * </ol>
	 *
	 * @param definitions
	 *  The list of definitions for which tasks need to be generated
	 * @return
	 * A sorted list of tasks
	 */
	public List<AssignedTask> getTaskStack(final @NonNull List<TaskDefinition> definitions,
		final @NonNull LocalDate startDate, final @NonNull LocalDate endDate)
	{
		final List<AssignedTask> tasks = new ArrayList<>();
		for (final TaskDefinition def : definitions)
		{
			final List<LocalDate> blockEndDates = getPeriodBlocks(startDate, endDate, def.getPeriodType());

			for (final LocalDate date : blockEndDates)
			{
				final int taskDays = (int) Math.floor(repetitionTypeToDuration(def.getPeriodType()).getDays() / def.getFrequency());
				for (int i = 0; i < def.getFrequency(); i++)
				{
					final AssignedTask task = new AssignedTask();
					task.setDefinition(def);
					task.setStatus(TaskStatus.TODO);
					task.setDueDate(date.minusDays(i * taskDays));

					tasks.add(task);
				}
			}
		}
		tasks.sort(Comparator.comparing(AssignedTask::getDueDate));
		return tasks;
	}

	public List<LocalDate> getPeriodBlocks(final @NonNull LocalDate startDate, final @NonNull LocalDate endDate,
		final @NonNull TaskRepetitionType type)
	{
		final TemporalAmount periodTypeDuration = repetitionTypeToDuration(type);
		final List<LocalDate> periodEndDates = new ArrayList<>();

		LocalDate currentDate = startDate.plus(periodTypeDuration);
		while (!currentDate.isAfter(endDate))
		{
			periodEndDates.add(currentDate);
			switch (type)
			{
			case DAY:
				currentDate = currentDate.plusDays(1);
				break;
			case WEEK:
				currentDate = currentDate.plusWeeks(1);
				break;
			case MONTH:
				currentDate = currentDate.plusMonths(1);
				break;
			case YEAR:
				currentDate = currentDate.plusYears(1);
				break;
			}
		}

		return periodEndDates;
	}

	private Period repetitionTypeToDuration(final TaskRepetitionType type)
	{
		switch (type)
		{
		case DAY:
			return Period.ofDays(1);
		case WEEK:
			return Period.ofWeeks(1);
		case MONTH:
			return Period.ofMonths(1);
		case YEAR:
			return Period.ofYears(1);
		default:
			throw new IllegalArgumentException("Unkown TaskRepetitionType " + type);
		}
	}
}
