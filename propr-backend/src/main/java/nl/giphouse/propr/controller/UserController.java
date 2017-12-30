package nl.giphouse.propr.controller;

import java.security.Principal;

import javax.inject.Inject;
import javax.validation.Valid;

import nl.giphouse.propr.dto.user.UserDTO;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.model.user.UserFactory;
import nl.giphouse.propr.repository.UserRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haye.
 */
@RestController
@RequestMapping("/api/user")
public class UserController  extends AbstractProprController
{
	@Inject
	private UserRepository userRepository;

	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
	private UserFactory userFactory;

	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public ResponseEntity registerUser(@RequestBody @Valid final UserDTO userDto)
	{
		if (userRepository.findOneByUsername(userDto.getUsername()) != null)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
		}

		final User user = new User();
		user.setUsername(userDto.getUsername());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setEmail(userDto.getEmail());
		user.setFirstname(userDto.getFirstname());
		user.setLastname(userDto.getLastname());

		userRepository.save(user);

		return ResponseEntity.ok().build();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/info")
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

		return ResponseEntity.ok(userFactory.fromEntity(user));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{userId}/avatar")
	public ResponseEntity<byte[]> getUserAvatar(final @PathVariable("userId") Long userId)
	{
		final User user = userRepository.findOne(userId);
		final byte[] avatar = user.getAvatar();
		if (avatar == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		headers.setContentLength(avatar.length);
		headers.setCacheControl("max-age=3600");
		return new ResponseEntity<>(avatar, headers, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/avatar")
	public ResponseEntity<Void> updateUserAvatar(final Principal principal, final @RequestBody byte[] avatar)
	{
		final User user = userRepository.findOneByUsername(principal.getName());
		user.setAvatar(avatar);

		userRepository.save(user);
		return ResponseEntity.ok(null);
	}
}
