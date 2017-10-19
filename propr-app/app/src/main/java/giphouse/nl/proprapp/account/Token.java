package giphouse.nl.proprapp.account;

/**
 * @author haye
 */
public class Token {

	private String authToken;

	private String refreshToken;

	public Token(final String authToken, final String refreshToken) {
		this.authToken = authToken;
		this.refreshToken = refreshToken;
	}

	public String getAuthToken() {
		return authToken;
	}

	void setAuthToken(final String token) {
		this.authToken = token;
	}

	String getRefreshToken() {
		return refreshToken;
	}

	void setRefreshToken(final String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
