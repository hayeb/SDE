package nl.giphouse.propr.model.user;

import nl.giphouse.propr.dto.user.UserInfoDto;
import org.springframework.stereotype.Component;

/**
 * @author haye.
 */
@Component
public class UserFactory {

	public UserInfoDto fromEntity(final User user)
	{
		return UserInfoDto.builder()
			.username(user.getUsername())
			.firstname(user.getFirstname())
			.lastname(user.getLastname())
			.build();
	}
}
