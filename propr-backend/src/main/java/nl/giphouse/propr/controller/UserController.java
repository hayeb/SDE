package nl.giphouse.propr.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import nl.giphouse.propr.dto.user.UserDTO;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.UserRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haye.
 */
@RestController
@RequestMapping("/api/users")
public class UserController
{
	@Inject
	private UserRepository userRepository;

	@Inject
	private PasswordEncoder passwordEncoder;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity registerUser(@RequestBody @Valid final UserDTO userDto)
	{
		if (userRepository.findOneByUsername(userDto.getUsername()) != null)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
		}

		final User user = new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail(), userDto.getFirstname(),
			userDto.getLastname());
		// user.setRoles(Collections.singletonList("ROLE_USER"));

		userRepository.save(user);

		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public ResponseEntity getUserInfo(@RequestParam final String username)
	{
		if (StringUtils.isEmpty(username))
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No username was given");
		}

		final User user = userRepository.findOneByUsername(username);
		if (user == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		return ResponseEntity.ok(user);
	}

}
