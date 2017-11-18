package giphouse.nl.proprapp.service.task;

import java.util.List;

import nl.giphouse.propr.dto.task.TaskDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author haye
 */
public interface TaskService {

	@GET("api/task/group/user")
	Call<List<TaskDto>> getTasksForUserInGroup(final @Query("groupname") String groupname);

	@GET("api/task/group/done")
	Call<List<TaskDto>> getDoneTasksInGroup(final @Query("groupname") String groupname);
}
