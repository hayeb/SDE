package giphouse.nl.proprapp.account;

/**
 * @author haye
 */
public class UserAccountDto {
	private final String username;

	private final String password;

	private final String email;

	private final String firstname;

	private final String lastname;

	public UserAccountDto(final String username, final String password, final String email, final String firstname, final String lastname) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}
}
