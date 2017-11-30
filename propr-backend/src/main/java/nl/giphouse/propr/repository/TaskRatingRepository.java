package nl.giphouse.propr.repository;

import nl.giphouse.propr.model.task.TaskRating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author haye.
 */
@Repository
public interface TaskRatingRepository extends JpaRepository<TaskRating, Long>
{
}
