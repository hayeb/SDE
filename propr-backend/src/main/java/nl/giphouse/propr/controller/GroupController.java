package nl.giphouse.propr.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import nl.giphouse.propr.model.Group;
import nl.giphouse.propr.model.GroupAddDto;
import nl.giphouse.propr.model.GroupDto;
import nl.giphouse.propr.model.GroupJoinDto;
import nl.giphouse.propr.model.User;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haye.
 */
@RestController
@RequestMapping("/api/group")
@Slf4j
public class GroupController
{
	@Inject
	private UserService userService;

	@Inject
	private GroupRepository groupRepository;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<GroupDto>> listGroups(final Principal principal)
	{
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

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<GroupDto> createGroup(@RequestBody final GroupAddDto groupAddDto, final Principal principal)
	{
		if (groupRepository.countByName(groupAddDto.getGroupName()) > 0)
		{
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
		}

		final User user = (User) userService.loadUserByUsername(principal.getName());

		final Group group = new Group();
		group.setAdmin(user);
		group.setName(groupAddDto.getGroupName());
		group.setInviteCode(groupAddDto.getGroupCode());
		group.setUsers(Collections.singletonList(user));
		log.info("Saving images not implemented yet.");
		groupRepository.save(group);

		return ResponseEntity.ok(GroupDto.fromGroup(group));
	}

	@RequestMapping(value = "/join", method = RequestMethod.POST)
	public ResponseEntity<Void> joinGroup(@RequestBody final GroupJoinDto groupJoinDto, final Principal principal)
	{
		if (groupRepository.countByName(groupJoinDto.getGroupName()) == 0)
		{
			return ResponseEntity.notFound().build();
		}

		final Group group = groupRepository.findGroupByName(groupJoinDto.getGroupName());

		if (!group.getInviteCode().equals(groupJoinDto.getEnteredCode()))
		{
			return ResponseEntity.unprocessableEntity().body(null);
		}

		final User user = (User) userService.loadUserByUsername(principal.getName());

		if (group.getUsers().contains(user))
		{
			return ResponseEntity.badRequest().body(null);
		}

		group.getUsers().add(user);
		groupRepository.save(group);
		return ResponseEntity.ok(null);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ResponseEntity<List<GroupDto>> searchGroups(@RequestParam final String query)
	{
		if (StringUtils.isEmpty(query))
		{
			return ResponseEntity.badRequest().body(null);
		}

		final List<Group> foundGroups = groupRepository.findGroupsByNameIsContaining(query);
		final List<GroupDto> groupDtos = foundGroups.stream()
			.map(GroupDto::fromGroup)
			.collect(Collectors.toList());

		return ResponseEntity.ok(groupDtos);
	}
}
