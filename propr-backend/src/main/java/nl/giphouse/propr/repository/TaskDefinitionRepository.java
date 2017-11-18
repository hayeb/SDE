package nl.giphouse.propr.repository;

import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.TaskDefinition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author haye.
 */
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long>
{
	List<TaskDefinition> findAllByGroup(final Group group);
}
