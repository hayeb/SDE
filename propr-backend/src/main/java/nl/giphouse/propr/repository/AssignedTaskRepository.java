package nl.giphouse.propr.repository;

import java.util.List;

import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.user.User;

/**
 * @author haye
 */
public interface AssignedTaskRepository
{
	List<AssignedTask> findTasksForUserInGroup(Group group, User user);

	List<AssignedTask> findTodoTasksInGroup(Group group);

	List<AssignedTask> findActivityInGroup(Group group);
}
