package giphouse.nl.proprapp.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye
 */
@Getter
@AllArgsConstructor
public class Token {

	private final String authToken;

	private final String refreshToken;
}
