package giphouse.nl.proprapp.service.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye
 */
@AllArgsConstructor
@Getter
public class UserInfoDto {

	private final String username;

	private final String firstname;

	private final String lastname;

	private final String email;
}
