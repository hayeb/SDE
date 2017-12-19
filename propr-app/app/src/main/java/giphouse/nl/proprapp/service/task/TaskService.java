package giphouse.nl.proprapp.service.task;

import java.util.List;

import nl.giphouse.propr.dto.task.TaskCompletionDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskDto;
import nl.giphouse.propr.dto.task.TaskRatingDto;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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

	@POST("api/task/group/add")
	Call<TaskDefinitionDto> createTask(final @Body TaskDefinitionDto taskDefinitionDto);

	@POST("api/task/{taskId}/complete")
	Call<Void> completeTask(@Path("taskId") Long taskId, @Body TaskCompletionDto taskCompletionDto);

	@POST("api/task/{taskId}/image")
	@Headers("Content-Type: image/jpeg")
	Call<Void> uploadImage(@Path("taskId") long taskId, @Body RequestBody image);

	@POST("api/task/{taskId}/rate")
	Call<Void> rateTask(@Path("taskId") long taskId, @Body TaskRatingDto taskRatingDto);

	@GET("api/task/{taskId}/rate")
	Call<TaskRatingDto> getTaskRating(@Path("taskId") long taskId);

	@GET("api/task/{taskId}/ratings")
	Call<List<TaskRatingDto>> getRatingsForTask(@Path("taskId") long taskId);

	@GET("api/task/{taskId}/average")
	Call<Double> getAverageRatingForTask(@Path("taskId") long taskId);

}
