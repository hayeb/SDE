package nl.giphouse.propr.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author haye.
 */
@AllArgsConstructor
@Getter
@Builder
public class UserInfoDto {

	private final String username;

	private final String firstname;

	private final String lastname;

	private final String email;
}
