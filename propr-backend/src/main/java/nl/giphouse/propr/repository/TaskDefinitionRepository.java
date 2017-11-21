package nl.giphouse.propr.repository;

import java.util.List;

import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.TaskDefinition;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author haye.
 */
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long>
{
	List<TaskDefinition> findAllByGroup(final Group group);
}
