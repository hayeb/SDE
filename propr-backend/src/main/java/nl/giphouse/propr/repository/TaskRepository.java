package nl.giphouse.propr.repository;

import java.time.LocalDate;
import java.util.List;

import nl.giphouse.propr.dto.task.TaskStatus;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author haye
 */
public interface TaskRepository extends JpaRepository<AssignedTask, Long>, AssignedTaskRepository
{
	List<AssignedTask> findAllByAssigneeAndDefinitionGroup(final User assignee, final Group group);

	List<AssignedTask> findAllByDefinitionGroupAndDueDateGreaterThanEqualAndCompletedTaskIsNotNull(final Group group, final LocalDate localDate);

	List<AssignedTask> findAllByDefinitionGroupAndDueDateGreaterThanEqualAndCompletedTaskIsNull(final Group group, final LocalDate localDate);
}
