package giphouse.nl.proprapp.service.group;

import java.util.List;

import giphouse.nl.proprapp.service.group.search.GroupSearchResult;
import nl.giphouse.propr.dto.group.GroupAddDto;
import nl.giphouse.propr.dto.group.GroupDto;
import nl.giphouse.propr.dto.group.GroupJoinDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.user.UserInfoDto;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
public interface GroupService {

	@GET("api/group")
	Call<List<GroupDto>> listGroups();

	@POST("api/group")
	Call<GroupDto> createGroup(@Body final GroupAddDto groupAddDto);

	@POST("api/group/join")
	Call<GroupDto> joinGroup(@Body final GroupJoinDto groupJoinDto);

	@GET("api/group/{groupId}/users")
	Call<List<UserInfoDto>> getUsersInGroup(@Path("groupId") final long groupId);

	@POST("api/group/{groupId}/leave")
	Call<Void> leaveGroup(@Path("groupId") long groupId);

	@GET("api/group/{groupId}/image")
	Call<byte[]> getGroupImage(@Path("groupId") long groupId);

	@POST("api/group/{groupId}/image")
	@Headers("Content-Type: image/jpeg")
	Call<Void> updateGroupImage(@Path("groupId") long groupId, @Body RequestBody image);
}
