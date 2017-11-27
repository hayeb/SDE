package giphouse.nl.proprapp.service.group;

import java.util.List;

import giphouse.nl.proprapp.service.group.search.GroupSearchResult;
import nl.giphouse.propr.dto.group.GroupAddDto;
import nl.giphouse.propr.dto.group.GroupDto;
import nl.giphouse.propr.dto.group.GroupJoinDto;
import nl.giphouse.propr.dto.user.UserInfoDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author haye
 */
public interface GroupService {

	@GET("api/group")
	Call<List<GroupDto>> listGroups();

	@POST("api/group/create")
	Call<GroupDto> createGroup(@Body final GroupAddDto groupAddDto);


	@POST("api/group/join")
	Call<GroupDto> joinGroup(@Body final GroupJoinDto groupJoinDto);

	@GET("api/group/search")
	Call<List<GroupSearchResult>> searchGroups(@Query("query") final String query);

	@GET("api/group/users")
	Call<List<UserInfoDto>> getUsersInGroup(@Query("groupName") final String groupName);

	@POST("api/group/{groupId}/leave")
	Call<Void> leaveGroup(@Path("groupId") long groupId);
}
