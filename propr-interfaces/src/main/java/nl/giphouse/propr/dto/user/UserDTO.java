package nl.giphouse.propr.dto.user;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haye.
 */
@Builder
@Getter
@Setter
public class UserDTO
{
	@NotNull
	private String username;

	@NotNull
	private String password;

	@NotNull
	private String email;

	@NotNull
	private String firstname;

	@NotNull
	private String lastname;

}
