package giphouse.nl.proprapp.service.user;

import nl.giphouse.propr.dto.user.UserInfoDto;
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
public interface UserService {

	@GET("api/user/info")
	Call<UserInfoDto> getUserInfo(@Query("username") String username);

	@POST("api/user/avatar")
	@Headers("Content-Type: image/jpeg")
	Call<Void> updateUserAvatar(@Body RequestBody image);
}
