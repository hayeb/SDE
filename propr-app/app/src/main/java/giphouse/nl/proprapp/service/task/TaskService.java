package giphouse.nl.proprapp.service.task;

import java.util.List;

import nl.giphouse.propr.dto.task.TaskCompletionDto;
import nl.giphouse.propr.dto.task.TaskDto;
import nl.giphouse.propr.dto.task.TaskRatingDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author haye
 */
public interface TaskService {

	@GET("api/task/group/user")
	Call<List<TaskDto>> getTasksForUserInGroup(@Query("groupname") String groupname);

	@GET("api/task/group/activity")
	Call<List<TaskDto>> getDoneTasksInGroup(@Query("groupname") String groupname);

	@GET("api/task/group/scheduled")
	Call<List<TaskDto>> getTodoTasksInGroup(@Query("groupname") String groupname);

	@POST("api/task/{taskId}/complete")
	Call<Void> completeTask(@Path("taskId") Long taskId, @Body TaskCompletionDto taskCompletionDto);

	@POST("api/task/{taskId}/image")
	Call<Void> uploadImage(@Path("taskId") long taskId, @Body byte[] image);

	@POST("api/task/{taskId}/rate")
	Call<Void> rateTask(@Path("taskId") long taskId, @Body TaskRatingDto taskRatingDto);

	@GET("api/task/{taskId}/rate")
	Call<TaskRatingDto> getTaskRating(@Path("taskId") long taskId);

}