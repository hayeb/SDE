package nl.giphouse.propr.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import nl.giphouse.propr.dto.group.GroupAddDto;
import nl.giphouse.propr.dto.group.GroupDto;
import nl.giphouse.propr.dto.group.GroupJoinDto;
import nl.giphouse.propr.dto.user.UserInfoDto;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.group.GroupFactory;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.model.user.UserFactory;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.repository.TaskRepository;
import nl.giphouse.propr.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/group")
@Slf4j
public class GroupController
{
	@Inject
	private UserService userService;

	@Inject
	private GroupRepository groupRepository;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private GroupFactory groupFactory;

	@Inject
	private UserFactory userFactory;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<GroupDto>> listGroups(final Principal principal)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());

		log.debug("Handling /api/group");

		final List<Group> groups = groupRepository.findGroupsByUsers(user);

		final List<GroupDto> dtos = groups.stream()
			.map(groupFactory::fromEntity)
			.collect(Collectors.toList());

		return ResponseEntity.ok(dtos);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createGroup(@RequestBody final GroupAddDto groupAddDto, final Principal principal)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());

		if (groupRepository.countByName(groupAddDto.getGroupName()) > 0)
		{
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Group with this name already exists.");
		}

		log.debug("Handling /api/group/create");

		final Group group = new Group();
		group.setAdmin(user);
		group.setName(groupAddDto.getGroupName());
		group.setInviteCode(groupAddDto.getGroupCode());
		group.setUsers(Collections.singletonList(user));
		log.info("Saving images not implemented yet.");
		groupRepository.save(group);

		return ResponseEntity.ok(groupFactory.fromEntity(group));
	}

	@RequestMapping(value = "/{groupId}/users", method = RequestMethod.GET)
	public ResponseEntity<List<UserInfoDto>> listUsersInGroup(final Principal principal, final @PathVariable long groupId)
	{
		final Group group = groupRepository.findOne(groupId);

		log.debug("Handling /api/group/users");

		if (group == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		final User user = (User) userService.loadUserByUsername(principal.getName());
		if (!group.getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		return ResponseEntity.ok(group.getUsers().stream().map(userFactory::fromEntity).collect(Collectors.toList()));
	}

	@RequestMapping(value = "/join", method = RequestMethod.POST)
	public ResponseEntity<GroupDto> joinGroup(@RequestBody final GroupJoinDto groupJoinDto, final Principal principal)
	{
		// no group found, code is not valid.
		if (groupRepository.countByInviteCode(groupJoinDto.getEnteredCode()) == 0)
		{
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
		}

		// group exists
		final Group group = groupRepository.findGroupByInviteCode(groupJoinDto.getEnteredCode());
		final User user = (User) userService.loadUserByUsername(principal.getName());
		log.debug("Handling /api/group/join");

		if (group.getUsers().contains(user))
		{
			return ResponseEntity.badRequest().body(null);
		}

		group.getUsers().add(user);
		groupRepository.save(group);

		return ResponseEntity.ok(groupFactory.fromEntity(group));
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ResponseEntity<List<GroupDto>> searchGroups(@RequestParam final String query)
	{
		if (StringUtils.isEmpty(query))
		{
			return ResponseEntity.badRequest().body(null);
		}

		log.debug("Handling /api/group/search");

		final List<Group> foundGroups = groupRepository.findGroupsByNameIsContaining(query);
		final List<GroupDto> groupDtos = foundGroups.stream()
			.map(groupFactory::fromEntity)
			.collect(Collectors.toList());

		return ResponseEntity.ok(groupDtos);
	}

	@RequestMapping(value = "/{groupId}/leave", method = RequestMethod.POST)
	public ResponseEntity<Void> leaveGroup(final Principal principal, final @PathVariable("groupId") long groupId)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());
		final Group group = groupRepository.findOne(groupId);
		if (group == null)
		{
			return ResponseEntity.notFound().build();
		}

		// TODO: Handle admin leaving group. Is disallowing enough?
		if (group.getAdmin().equals(user))
		{
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}

		group.getUsers().remove(user);

		taskRepository.delete(taskRepository.findAllByAssigneeAndDefinitionGroup(user, group));

		return ResponseEntity.ok(null);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{groupId}/image")
	public ResponseEntity<Void> updateGroupImage(final Principal principal, final @PathVariable long groupId, final @RequestBody byte[] image)
	{
		log.debug("Handling POST /api/group/{}/image", groupId);
		final Group group = groupRepository.findOne(groupId);
		if (group == null)
		{
			return ResponseEntity.notFound().build();
		}
		final User user = (User) userService.loadUserByUsername(principal.getName());
		if (!group.getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		group.setImage(image);

		groupRepository.save(group);

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))	.body(null);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{groupId}/image")
	public ResponseEntity<byte[]> getGroupImage(final Principal principal, final @PathVariable long groupId)
	{
		log.debug("Handling GET /api/group/{}/image", groupId);
		final Group group = groupRepository.findOne(groupId);
		if (group == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		final User user = (User) userService.loadUserByUsername(principal.getName());
		if (!group.getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		return ResponseEntity.ok(group.getImage());
	}
}
