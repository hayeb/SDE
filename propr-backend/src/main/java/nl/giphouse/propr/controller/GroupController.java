package nl.giphouse.propr.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import nl.giphouse.propr.dto.group.GroupAddDto;
import nl.giphouse.propr.dto.group.GroupDto;
import nl.giphouse.propr.dto.group.GroupJoinDto;
import nl.giphouse.propr.dto.group.GenerateScheduleDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.user.UserInfoDto;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.group.GroupFactory;
import nl.giphouse.propr.model.task.TaskDefinition;
import nl.giphouse.propr.model.task.TaskFactory;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.model.user.UserFactory;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import nl.giphouse.propr.repository.TaskRepository;
import nl.giphouse.propr.service.ScheduleService;
import nl.giphouse.propr.service.SchedulingResult;
import nl.giphouse.propr.service.UserService;
import nl.giphouse.propr.utils.ValidationUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	private ScheduleService scheduleService;

	@Inject
	private GroupRepository groupRepository;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private TaskDefinitionRepository taskDefinitionRepository;

	@Inject
	private GroupFactory groupFactory;

	@Inject
	private UserFactory userFactory;

	@Inject
	private TaskFactory tasksFactory;

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

		if (!ValidationUtils.isValidJPEG(image))
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

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

		return ResponseEntity.ok().body(null);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{groupId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
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

		if (group.getImage() == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		headers.setContentLength(group.getImage().length);
		headers.setCacheControl("max-age=3600");

		return new ResponseEntity<>(group.getImage(), headers, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{groupId}/schedule")
	public ResponseEntity<String> generateSchedule(final Principal principal, final @PathVariable long groupId,
		final @RequestBody GenerateScheduleDto generateScheduleDto)
	{
		final Group group = groupRepository.findGroupById(groupId);
		if (group == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		final User user = (User) userService.loadUserByUsername(principal.getName());
		if (!group.getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		final SchedulingResult result = scheduleService.reschedule(group, LocalDate.now(),
			LocalDate.now().plusDays(generateScheduleDto.getNumberOfDays()));

		if (!result.isSuccesfull())
		{
			return ResponseEntity.ok(result.getMessage());
		}

		return ResponseEntity.ok(null);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{groupId}/schedule")
	public ResponseEntity<Map<TaskRepetitionType, List<TaskDefinitionDto>>> getGroupSchedule(final Principal principal, final @PathVariable long groupId)
	{
		final Group group = groupRepository.findGroupById(groupId);
		if (group == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		final User user = (User) userService.loadUserByUsername(principal.getName());
		if (!group.getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		final Map<TaskRepetitionType, List<TaskDefinitionDto>> result = taskDefinitionRepository.findAllByGroup(group).stream()
			.collect(Collectors.groupingBy(TaskDefinition::getPeriodType, Collectors.mapping(tasksFactory::toTaskDefinitionDto, Collectors.toList())));

		return ResponseEntity.ok(result);
	}
}
