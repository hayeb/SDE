package nl.giphouse.propr;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * @author haye.
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter
{

	@Inject
	private UserDetailsService userDetailsService;

	@Inject
	private AuthenticationManager authenticationManager;

	@Inject
	private ProprConfiguration proprConfiguration;

	@Value("${security.oauth2.resource.id}")
	private String resourceId;

	@Value("${spring.datasource.url}")
	private String oauthUrl;

	@Value("${spring.datasource.username}")
	private String user;

	@Value("${spring.datasource.password}")
	private String password;

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer configurer)
	{
		configurer.authenticationManager(authenticationManager)
			.userDetailsService(userDetailsService)
			.tokenServices(tokenServices())
			.tokenStore(tokenStore());
	}

	@Override
	public void configure(final AuthorizationServerSecurityConfigurer security)
	{
		security.checkTokenAccess("hasAuthority('ROLE_USER')");
	}

	@Override
	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception
	{
		clients.inMemory()
			.withClient(proprConfiguration.getClientId())
			.accessTokenValiditySeconds(60 * 60 * 24)
			.refreshTokenValiditySeconds(60 * 60 * 24 * 48)
			.authorities("ROLE_USER")
			.scopes("read", "write")
			.resourceIds(resourceId)
			.authorizedGrantTypes("password", "refresh_token", "client_credentials").resourceIds("resource")
			.secret(proprConfiguration.getClientSecret());
	}

	@Bean
	public TokenStore tokenStore()
	{
		final DataSource tokenDatasource = DataSourceBuilder.create()
			.driverClassName("org.postgresql.Driver")
			.username(user)
			.password(password)
			.url(oauthUrl)
			.build();
		return new JdbcTokenStore(tokenDatasource);
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices()
	{
		final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

}
