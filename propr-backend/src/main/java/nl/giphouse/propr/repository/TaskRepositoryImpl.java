package nl.giphouse.propr.repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.CompletedTask;
import nl.giphouse.propr.model.user.User;

import org.springframework.stereotype.Service;

/**
 * @author haye
 */
@Service
public class TaskRepositoryImpl implements AssignedTaskRepository
{
	private static final String USER_TASKS_QUERY = "SELECT task from AssignedTask task JOIN task.definition def " +
		"WHERE def.group = :group AND task.assignee = :assignee" +
		" AND (task.completedTask IS NULL)" +
		" ORDER BY task.dueDate ASC";

	private static final String GROUP_TASKS_TODO_QUERY = "SELECT task FROM AssignedTask task JOIN task.definition def " +
		" WHERE def.group = :group AND task.completedTask IS NULL AND task.dueDate >= :date" +
		" ORDER BY task.dueDate ASC";

	private static final String GROUP_ACTIVITY_QUERY = "SELECT task FROM AssignedTask task JOIN task.definition def " +
		"WHERE def.group = :group AND (task.completedTask IS NOT NULL OR task.completedTask IS NULL AND task.dueDate < :date)";

	@Inject
	private EntityManager entityManager;

	@Override
	public List<AssignedTask> findTasksForUserInGroup(final Group group, final User user)
	{
		final TypedQuery<AssignedTask> query = entityManager.createQuery(USER_TASKS_QUERY, AssignedTask.class);
		query.setParameter("group", group);
		query.setParameter("assignee", user);
		query.setMaxResults(25);
		return query.getResultList();
	}

	@Override
	public List<AssignedTask> findTodoTasksInGroup(final Group group)
	{
		final TypedQuery<AssignedTask> query = entityManager.createQuery(GROUP_TASKS_TODO_QUERY, AssignedTask.class);
		query.setParameter("group", group);
		query.setParameter("date", LocalDate.now());
		query.setMaxResults(25);
		return query.getResultList();
	}

	@Override
	public List<AssignedTask> findActivityInGroup(final Group group)
	{
		final TypedQuery<AssignedTask> query = entityManager.createQuery(GROUP_ACTIVITY_QUERY, AssignedTask.class);
		query.setParameter("group", group);
		query.setParameter("date", LocalDate.now());
		query.setMaxResults(50);

		final List<AssignedTask> tasks = query.getResultList();
		tasks.sort(Comparator.comparing(t -> Optional.ofNullable(t.getCompletedTask()).map(CompletedTask::getDate).orElse(t.getDueDate()),
			Comparator.reverseOrder()));

		return tasks;
	}
}
