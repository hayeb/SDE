package nl.giphouse.huishoudbackend.api;

import lombok.extern.slf4j.Slf4j;
import nl.giphouse.huishoudbackend.entity.user.User;
import nl.giphouse.huishoudbackend.entity.user.UserRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * @author haye.
 */
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class UserRestController {

	@Inject
	private UserRepository userRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/all")
	public List<User> getUsers() {
		return IteratorUtils.toList(userRepository.findAll().iterator());
	}

	@RequestMapping(method = RequestMethod.POST,
			value = "/add",
			consumes = "application/json",
			produces = "application/json")
	public User addUser(@RequestBody final User user) {
		return userRepository.save(user);
	}

}
