package nl.giphouse.propr.model;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import nl.giphouse.propr.util.ValidEmail;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author haye.
 */
@Getter
@Setter
public class UserDTO {
	@NotNull
	@NotEmpty
	private String username;

	@NotNull
	@NotEmpty
	private String password;

	@NotNull
	@NotEmpty
	@ValidEmail
	private String email;

	@NotNull
	@NotEmpty
	private String firstname;

	@NotNull
	@NotEmpty
	private String lastname;
}
