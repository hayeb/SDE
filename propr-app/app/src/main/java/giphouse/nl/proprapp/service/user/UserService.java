package giphouse.nl.proprapp.service.user;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author haye
 */
public interface UserService {

	@GET("api/users/info")
	Call<UserInfoDto> getUserInfo(@Query("username") String username);
}
