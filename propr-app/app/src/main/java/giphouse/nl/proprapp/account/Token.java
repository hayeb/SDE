package giphouse.nl.proprapp.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye
 */
public class Token {

	private final String authToken;

	private final String refreshToken;

	public Token(final String authToken, final String refreshToken) {
		this.authToken = authToken;
		this.refreshToken = refreshToken;
	}

	public String getAuthToken() {
		return authToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
