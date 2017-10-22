package giphouse.nl.proprapp.service.group;

import java.util.List;

import giphouse.nl.proprapp.service.group.model.GroupAddDto;
import giphouse.nl.proprapp.service.group.model.GroupDto;
import giphouse.nl.proprapp.service.group.model.GroupJoinDto;
import giphouse.nl.proprapp.service.group.search.GroupSearchResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author haye
 */
public interface GroupService {

	@GET("/api/group")
	Call<List<GroupDto>> listGroups();

	@POST("/api/group/create")
	Call<Void> createGroup(@Body final GroupAddDto groupAddDto);

	@POST("/api/group/join")
	Call<Void> joinGroup(@Body final GroupJoinDto groupJoinDto);

	@GET("/api/group/search")
	Call<List<GroupSearchResult>> searchGroups(@Query("query") final String query);
}
