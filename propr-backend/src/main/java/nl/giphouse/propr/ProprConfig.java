package nl.giphouse.propr;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author haye.
 */
@Configuration
@EnableResourceServer
public class ProprConfig extends ResourceServerConfigurerAdapter
{
	@Value("${security.oauth2.resource.id}")
	private String resourceId;

	@Inject
	private DefaultTokenServices tokenServices;

	@Inject
	private TokenStore tokenStore;

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources)
	{
		resources.resourceId(resourceId)
			.tokenServices(tokenServices)
			.tokenStore(tokenStore);
	}

	@Override
	public void configure(final HttpSecurity http) throws Exception
	{
		http
			.requestMatcher(new OAuthRequestedMatcher())
			.csrf().disable()
			.anonymous().disable()
			.authorizeRequests()
			.antMatchers(HttpMethod.OPTIONS).permitAll()
			.antMatchers("/api/**").authenticated();
	}

	private static class OAuthRequestedMatcher implements RequestMatcher
	{
		public boolean matches(final HttpServletRequest request)
		{
			// Determine if the resource called is "/api/**"
			String path = request.getServletPath();
			if (path.length() >= 5)
			{
				path = path.substring(0, 5);
				return path.equals("/api/");
			}
			else
				return false;
		}
	}
}
