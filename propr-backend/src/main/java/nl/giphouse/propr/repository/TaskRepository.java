package nl.giphouse.propr.repository;

import java.util.List;

import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.Task;
import nl.giphouse.propr.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author haye
 */
public interface TaskRepository extends JpaRepository<Task, Long>
{
	List<Task> findAllByAssigneeAndGroup(final User assignee, final Group group);
}
