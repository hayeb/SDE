package nl.giphouse.propr;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haye
 */
@Component
@ConfigurationProperties(prefix = "propr")
@Getter
@Setter
public class ProprConfiguration
{
	private String clientSecret;

	private String clientId;
}
