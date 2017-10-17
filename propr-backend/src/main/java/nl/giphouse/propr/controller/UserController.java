package nl.giphouse.propr.controller;

import nl.giphouse.propr.model.User;
import nl.giphouse.propr.model.UserDTO;
import nl.giphouse.propr.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * @author haye.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Inject
	private UserRepository userRepository;

	@Inject
	private PasswordEncoder passwordEncoder;

	@RequestMapping("/register")
	public ResponseEntity registerUser(@RequestBody @Valid final UserDTO userDto) {
		if (userRepository.findOneByUsername(userDto.getUsername()) != null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
		}

		final User user = new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail(), true);
		//user.setRoles(Collections.singletonList("ROLE_USER"));

		userRepository.save(user);

		return ResponseEntity.ok().build();
	}
}
