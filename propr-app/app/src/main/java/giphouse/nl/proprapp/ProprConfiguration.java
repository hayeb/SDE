package giphouse.nl.proprapp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye
 */
@AllArgsConstructor
@Getter
public class ProprConfiguration {

	private final String clientId;

	private final String clientSecret;

	private final String backendUrl;
}
