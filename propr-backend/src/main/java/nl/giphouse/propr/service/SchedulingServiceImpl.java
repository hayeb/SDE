package nl.giphouse.propr.service;

import java.time.LocalDate;
import java.time.Period;
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
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.TaskDefinition;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import nl.giphouse.propr.repository.TaskRepository;
import org.apache.commons.collections4.CollectionUtils;
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
	 * Reschedules all task definitions in a group for a given period. Tries to take into account:
	 * <ol>
	 * <li>The tasks which may have already been completed after the {@code startdate} of the new schedule. Users who have already completed tasks in
	 * this period will get less tasks until the distribution is balanced.</li>
	 * <li>The weight of tasks</li>
	 * <li>Users who have since been removed from the group</li>
	 * </ol>
	 *
	 * @param group
	 *            The group for which to reschedule tasks
	 * @param startDate
	 *            The startdate of the new schedule
	 * @param endDate
	 *            The enddate of the new schedule
	 */
	public SchedulingResult reschedule(@NonNull final Group group, @NonNull final LocalDate startDate, @NonNull final LocalDate endDate)
	{
		if (!startDate.isBefore(endDate))
		{
			return SchedulingResult.invalidPeriod();
		}
		if (CollectionUtils.isEmpty(group.getUsers()))
		{
			return SchedulingResult.noUsersInGroup();
		}

		// 1. Determine which tasks may have already been completed, they do not have to be planned again.
		final List<AssignedTask> doneTasksInNewSchedule = taskRepository.findAllByDefinitionGroupAndDueDateGreaterThanEqualAndCompletedTaskIsNotNull(group, startDate);

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
		taskRepository.delete(taskRepository.findAllByDefinitionGroupAndDueDateGreaterThanEqualAndCompletedTaskIsNull(group, startDate));

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

		return SchedulingResult.success(tasks);
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
	 * Builds a "task stack" from a list of task definitions. The tasks will fall in the period between {@code startDate} and {@code endDate}. The
	 * strategy for doing this is roughly the following:
	 * <ol>
	 * <li>For every definition, split the total period into blocks according to the period type (day, week, month, ...).</li>
	 * <li>For every block generated this way, start from the end of the block and add {@code freq} tasks according to the frequency in the
	 * definition. Starts from the back of the block, because we do not want to generate a due date on {@code startDate} if we can help it.</li>
	 * <li>At the end, sort the tasks according to their {@code dueDate}.</li>
	 * </ol>
	 *
	 * @param definitions
	 *            The list of definitions for which tasks need to be generated.
	 * @param startDate
	 *            The start date of the period, inclusive.
	 * @param endDate
	 *            The end date of the period, inclusive.
	 * @return A sorted list of tasks without assignees
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
				final int taskDays = (int) Math.floor(periodDays(def.getPeriodType()) / def.getFrequency());
				for (int i = 0; i < def.getFrequency(); i++)
				{
					final AssignedTask task = new AssignedTask();
					task.setDefinition(def);
					task.setDueDate(date.minusDays(i * taskDays));

					tasks.add(task);
				}
			}
		}
		tasks.sort(Comparator.comparing(AssignedTask::getDueDate));
		return tasks;
	}

	/**
	 * Splits the given period (from {@code startDate} to {@code endDate}) into blocks of the given type. For instance, for a period of 01-01-2017 to
	 * 31-01-2017 and periodtype WEEK, split the period into four weeks (01-01 - 07-01, 08-01 - 14-01, 15-01 - 21-01, 22-01 - 28-01)
	 *
	 * @param startDate
	 *            start date of the period, inclusive
	 * @param endDate
	 *            end date f the period, inclusive
	 * @param type
	 *            The type of blocks to be generated
	 * @return a list of end dates of the generated blocks, in ascending order.
	 */
	public List<LocalDate> getPeriodBlocks(final @NonNull LocalDate startDate, final @NonNull LocalDate endDate,
		final @NonNull TaskRepetitionType type)
	{
		final Period periodTypeDuration = repetitionTypeToDuration(type);
		final List<LocalDate> periodEndDates = new ArrayList<>();

		LocalDate currentDate = startDate.plus(periodTypeDuration);
		while (!currentDate.isAfter(endDate))
		{
			periodEndDates.add(currentDate);
			currentDate = currentDate.plus(periodTypeDuration);
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

	private int periodDays(final TaskRepetitionType type) {
		switch(type)
		{
			case DAY:
				return 1;
			case WEEK:
				return 7;
			case MONTH:
				return 30;
			case YEAR:
				return 365;
			default:
				throw new IllegalArgumentException("Unkown TaskRepetitionType " + type);
		}
	}
}
