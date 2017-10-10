package nl.giphouse.propr.controller;

import lombok.Getter;
import lombok.Setter;
import nl.giphouse.propr.util.ValidEmail;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

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
}
