package giphouse.nl.proprapp.service.task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author haye
 */
public interface TaskService {

	@GET("/api/task/group")
	Call<List<UserTaskDto>> getTasksForUserInGroup(final @Query("groupname") String groupname);
}
