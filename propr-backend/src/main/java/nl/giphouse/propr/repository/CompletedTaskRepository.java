package nl.giphouse.propr.repository;

import nl.giphouse.propr.model.task.CompletedTask;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author haye.
 */
public interface CompletedTaskRepository extends JpaRepository<CompletedTask, Long>
{
}
