package nl.giphouse.propr.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskDto;
import nl.giphouse.propr.dto.task.TaskStatus;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.TaskFactory;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import nl.giphouse.propr.repository.TaskRepository;
import nl.giphouse.propr.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haye
 */
@RestController
@RequestMapping("/api/task")
@Slf4j
public class TaskController
{
	@Inject
	private TaskRepository taskRepository;

	@Inject
	private UserService userService;

	@Inject
	private GroupRepository groupRepository;

	@Inject
	private TaskFactory taskFactory;

	@Inject
	private TaskDefinitionRepository taskDefinitionRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/group/user")
	public ResponseEntity<?> getTasksForUserInGroup(final Principal principal, final @RequestParam String groupname)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());
		final Group group = groupRepository.findGroupByName(groupname);

		final ResponseEntity authorizationResponse = checkAuthorized(user, group);
		if (authorizationResponse != null)
		{
			return authorizationResponse;
		}

		log.debug("Handling /api/task/group/user");

		final List<TaskDto> tasks = taskRepository.findAllByAssigneeAndDefinitionGroup(user, group)
			.stream()
			.map(taskFactory::fromEntity)
			.collect(Collectors.toList());

		return ResponseEntity.ok(tasks);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/group/done")
	public ResponseEntity<?> getDoneTasksInGroup(final Principal principal, final @RequestParam String groupname)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());
		final Group group = groupRepository.findGroupByName(groupname);

		final ResponseEntity authorizationResponse = checkAuthorized(user, group);
		if (authorizationResponse != null)
		{
			return authorizationResponse;
		}

		log.debug("Handling /api/task/group/done");

		return getTasksByStatus(group, Arrays.asList(TaskStatus.DONE, TaskStatus.OVERDUE));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/group/todo")
	public ResponseEntity<?> getTodoTasksInGroup(final Principal principal, final @RequestParam String groupname)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());
		final Group group = groupRepository.findGroupByName(groupname);

		final ResponseEntity authorizationResponse = checkAuthorized(user, group);
		if (authorizationResponse != null)
		{
			return authorizationResponse;
		}

		log.debug("Handling /api/task/group/todo");

		return getTasksByStatus(group, Collections.singletonList(TaskStatus.TODO));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/group/schedule")
	public ResponseEntity<?> getTaskScheduleForGroup(final Principal principal, final @RequestParam String groupname)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());
		final Group group = groupRepository.findGroupByName(groupname);

		final ResponseEntity authorizationResponse = checkAuthorized(user, group);
		if (authorizationResponse != null)
		{
			return authorizationResponse;
		}

		log.debug("Handling /api/task/group/schedule");

		final List<TaskDefinitionDto> definitions = taskDefinitionRepository.findAllByGroup(group)
			.stream()
			.map(taskFactory::fromEntity)
			.collect(Collectors.toList());

		return ResponseEntity.ok(definitions);
	}

	private ResponseEntity<List<TaskDto>> getTasksByStatus(final Group group, final List<TaskStatus> statuses)
	{
		final List<TaskDto> doneTasks = taskRepository.findAllByDefinitionGroupAndStatusIn(group, statuses)
			.stream()
			.sorted(Comparator.comparing(AssignedTask::getDueDate))
			.map(taskFactory::fromEntity)
			.collect(Collectors.toList());

		return ResponseEntity.ok(doneTasks);
	}

	private ResponseEntity<?> checkAuthorized(final User user, final Group group)
	{
		if (group == null)
		{
			return ResponseEntity.notFound().build();
		}

		if (!group.getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return null;
	}

}
