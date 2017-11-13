package nl.giphouse.propr.service;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Service benodigd om het
 * @author haye.
 */
@Component
@Slf4j
public class UserService implements UserDetailsService {

	@Inject
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		log.debug("Trying to load user " + username);
		if (StringUtils.isEmpty(username)) {
			throw new UsernameNotFoundException("Empty username");
		}
		final User user =  userRepository.findOneByUsername(username);

		if (user == null)
		{
			log.debug("User " + username + " not found");
			throw new UsernameNotFoundException("User " + username + " not found!");
		}
		return user;
	}
}
