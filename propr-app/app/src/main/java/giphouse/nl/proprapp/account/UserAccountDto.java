package giphouse.nl.proprapp.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author haye
 */
@Builder
@AllArgsConstructor
@Getter
public class UserAccountDto {
	final String username;

	final String password;

	final String email;

	final String firstname;

	final String lastname;
}
