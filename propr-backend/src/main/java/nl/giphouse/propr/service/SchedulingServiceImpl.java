package nl.giphouse.propr.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
	 * Reschedules all task definitions in a group for a given period. It is assumed that the day from which to reschedule is the current day.
	 * 
	 * @param group
	 *            The group for which to reschedule tasks
	 * @param date
	 *            The amount of time in days to reschedule the tasks for.
	 * @param days
	 *            The amount of days to reschedule for
	 */
	public void reschedule(final Group group, final LocalDate date, final int days)
	{
		// 1. For each user, count the number of tasks already done which have a due date >= current date.
		final Map<User, Integer> doneTasksInSchedule = taskRepository
			.findAllByDefinitionGroupAndDueDateGreaterThanEqualAndStatusIs(group, date, TaskStatus.DONE).stream()
			.collect(Collectors.groupingBy(AssignedTask::getAssignee,
				Collectors.summingInt(t -> t.getDefinition().getWeight().getValue())));

		// 2. Remove users from the map who have been removed from the group.
		doneTasksInSchedule.keySet().removeIf(user -> !group.getUsers().contains(user));

		// 2. Create a priority queue over users to record their number of tasks in the new schedule
		final PriorityQueue<Pair<User, Integer>> userQueue = new PriorityQueue<>(Comparator.comparing(Pair::getRight));
		doneTasksInSchedule.entrySet().stream()
			.map(entry -> Pair.of(entry.getKey(), entry.getValue()))
			.forEach(userQueue::add);

		// 3. Add all users
		group.getUsers().stream()
			.filter(user -> !doneTasksInSchedule.containsKey(user))
			.forEach(user -> userQueue.add(Pair.of(user, 0)));

		// 4. Remove all assigned tasks in the schedule
		taskRepository.delete(taskRepository.findAllByDefinitionGroupAndDueDateGreaterThanEqualAndStatusIs(group, date, TaskStatus.TODO));

		// 4. For each task definition, we collect the last date on which it was completed.
		final Map<TaskDefinition, LocalDate> lastCompleted = taskRepository
			.findAllByDefinitionGroupAndStatusIn(group, Collections.singletonList(TaskStatus.DONE)).stream()
			.collect(Collectors.groupingBy(AssignedTask::getDefinition,
				Collectors.mapping(AssignedTask::getDueDate, Collectors.collectingAndThen(Collectors.toList(), Collections::max))));

		// 5. Given all the task definitions, calculate the list of tasks needed to be done in ascending order of date.
		final List<AssignedTask> tasks = getTaskStack(taskDefinitionRepository.findAllByGroup(group), lastCompleted, date, date.plusDays(days));

		// 6. Assign each task to a user. We update the total task weight for each user, so that we can distribute the tasks evenly.
		for (final AssignedTask task : tasks)
		{
			final Pair<User, Integer> userTasks = userQueue.poll();

			task.setAssignee(userTasks.getLeft());
			userQueue.add(Pair.of(userTasks.getLeft(), userTasks.getRight() + task.getDefinition().getWeight().getValue()));
		}

		// 6. Save all task definitions.
		taskRepository.save(tasks);
	}

	public List<AssignedTask> getTaskStack(final List<TaskDefinition> definitions, final Map<TaskDefinition, LocalDate> lastCompleted,
		final LocalDate startDate, final LocalDate endDate)
	{
		final List<AssignedTask> tasks = new ArrayList<>();
		for (final TaskDefinition def : definitions)
		{
			// Calculate the length of the period according to the last time the task was completed, of the start date of scheduling.
			final LocalDate lastCompletedDate = lastCompleted.getOrDefault(def, startDate);
			final List<LocalDate> blockStartDates = getPeriodBlocks(lastCompletedDate, endDate, def.getPeriodType());

			for (final LocalDate date : blockStartDates)
			{
				final int taskDays = (int) Math.floor(repetitionTypeToDuration(def.getPeriodType()).getDays() / def.getFrequency());
				for (int i = 0; i < def.getFrequency(); i++)
				{
					final AssignedTask task = new AssignedTask();
					task.setDefinition(def);
					task.setStatus(TaskStatus.TODO);
					task.setDueDate(date.plusDays(i * taskDays));

					tasks.add(task);
				}
			}
		}
		tasks.sort(Comparator.comparing(AssignedTask::getDueDate));
		return tasks;
	}

	public List<LocalDate> getPeriodBlocks(final LocalDate startDate, final LocalDate endDate, final TaskRepetitionType type)
	{
		final TemporalAmount periodTypeDuration = repetitionTypeToDuration(type);
		final List<LocalDate> periodStartDates = new ArrayList<>();

		LocalDate currentDate = startDate;
		while (!currentDate.plus(periodTypeDuration).isAfter(endDate))
		{
			periodStartDates.add(currentDate);
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

		return periodStartDates;
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
