package nl.giphouse.propr.controller;

import javax.inject.Inject;

import nl.giphouse.propr.model.Group;
import nl.giphouse.propr.model.GroupDto;
import nl.giphouse.propr.model.User;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.repository.UserRepository;
import nl.giphouse.propr.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haye.
 */
@RestController
@RequestMapping("/api/group")
public class GroupController {

	@Inject
	private UserService userService;

	@Inject
	private GroupRepository groupRepository;

	@RequestMapping("/create")
	public ResponseEntity createGroup(){
		return ResponseEntity.ok("ok");
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<GroupDto>> listGroups(final Principal principal) {
		final User user = (User) userService.loadUserByUsername(principal.getName());

		final List<Group> groups = groupRepository.findGroupsByUsers(user);

		if (CollectionUtils.isEmpty(groups))
		{
			return ResponseEntity.ok(Collections.emptyList());
		}

		final List<GroupDto> dtos = groups.stream()
			.map(group -> new GroupDto(group.getName(), group.getAdmin().getUsername(),
					group.getUsers().stream().map(User::getUsername).collect(Collectors.toList())))
			.collect(Collectors.toList());

		return ResponseEntity.ok(dtos);
	}
}
