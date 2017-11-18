package nl.giphouse.propr.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import nl.giphouse.propr.dto.task.TaskDto;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.TaskFactory;
import nl.giphouse.propr.dto.task.TaskStatus;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.repository.TaskRepository;
import nl.giphouse.propr.service.UserService;

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

	@RequestMapping(method = RequestMethod.GET, value = "/group")
	public ResponseEntity<?> getTasksForUserInGroup(final Principal principal, final @RequestParam String groupname)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());
		final Group group = groupRepository.findGroupByName(groupname);
		if (group == null)
		{
			return ResponseEntity.notFound().build();
		}

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
		if (group == null)
		{
			return ResponseEntity.notFound().build();
		}

		if (!group.getUsers().contains(user))
		{
			return ResponseEntity.status(403).build();
		}

		final List<TaskDto> doneTasks = taskRepository.findAllByDefinitionGroupAndStatusIn(group, Arrays.asList(TaskStatus.DONE, TaskStatus.OVERDUE)).stream()
			.map(taskFactory::fromEntity)
			.collect(Collectors.toList());

		return ResponseEntity.ok(doneTasks);
	}

}
