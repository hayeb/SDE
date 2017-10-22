package giphouse.nl.proprapp.service.group;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author haye
 */
public interface GroupService {

	@GET("/api/group")
	Call<List<GroupDto>> listGroups();

	@POST("/api/group/create")
	Call<Void> createGroup(@Body final GroupAddDto groupAddDto);
}
