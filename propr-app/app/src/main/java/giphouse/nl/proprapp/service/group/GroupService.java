package giphouse.nl.proprapp.service.group;

import java.util.List;
import java.util.Map;

import nl.giphouse.propr.dto.group.GenerateScheduleDto;
import nl.giphouse.propr.dto.group.GroupAddDto;
import nl.giphouse.propr.dto.group.GroupDto;
import nl.giphouse.propr.dto.group.GroupJoinDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.user.UserInfoDto;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author haye
 */
public interface GroupService {

	@GET("api/group")
	Call<List<GroupDto>> listGroups();

	@POST("api/group")
	Call<GroupDto> createGroup(@Body GroupAddDto groupAddDto);

	@POST("api/group/join")
	Call<GroupDto> joinGroup(@Body GroupJoinDto groupJoinDto);

	@GET("api/group/{groupId}/users")
	Call<List<UserInfoDto>> getUsersInGroup(@Path("groupId") Long groupId);

	@POST("api/group/{groupId}/leave")
	Call<Void> leaveGroup(@Path("groupId") Long groupId);

	@POST("api/group/{groupId}/image")
	@Headers("Content-Type: image/jpeg")
	Call<Void> updateGroupImage(@Path("groupId") Long groupId, @Body RequestBody image);

	@POST("api/group/{groupId}/schedule")
	Call<Void> rescheduleGroup(@Path("groupId") Long groupId, @Body GenerateScheduleDto dto);

	@GET("api/group/{groupId}/schedule")
	Call<Map<TaskRepetitionType, List<TaskDefinitionDto>>> getGroupSchedule(@Path("groupId") Long groupId);
}