package nl.giphouse.propr.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import nl.giphouse.propr.dto.task.TaskCompletionDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskDto;
import nl.giphouse.propr.dto.task.TaskStatus;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.CompletedTask;
import nl.giphouse.propr.model.task.TaskFactory;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.CompletedTaskRepository;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import nl.giphouse.propr.repository.TaskRepository;
import nl.giphouse.propr.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

	@Inject
	private CompletedTaskRepository completedTaskRepository;

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
			.filter(t -> t.getStatus() == TaskStatus.TODO || t.getStatus() == TaskStatus.OVERDUE)
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

		return ResponseEntity.ok(getTasksByStatus(group, Arrays.asList(TaskStatus.DONE, TaskStatus.OVERDUE), null));
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

		return ResponseEntity.ok(getTasksByStatus(group, Collections.singletonList(TaskStatus.TODO), user));
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

	@Transactional(propagation = Propagation.REQUIRED)
	@RequestMapping(method = RequestMethod.POST, value = "/{taskId}/complete")
	public ResponseEntity<?> completeTask(final Principal principal, final @PathVariable Long taskId,
		final @RequestBody TaskCompletionDto taskCompletionDto)
	{
		final AssignedTask task = taskRepository.findOne(taskId);

		if (task == null)
		{
			return ResponseEntity.notFound().build();
		}

		final User user = (User) userService.loadUserByUsername(principal.getName());

		final ResponseEntity authorizationResponse = checkAuthorized(user, task.getDefinition().getGroup());
		if (authorizationResponse != null)
		{
			return authorizationResponse;
		}

		if (!task.getAssignee().equals(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		if (task.getStatus() == TaskStatus.DONE)
		{
			return ResponseEntity.badRequest().build();
		}

		final CompletedTask completedTask = new CompletedTask();
		completedTask.setDate(LocalDate.now());
		completedTask.setDescription(taskCompletionDto.getTaskCompletionDescription());
		completedTask.setImage(taskCompletionDto.getTaskComppletionImage());

		completedTaskRepository.save(completedTask);

		task.setCompletedTask(completedTask);
		task.setStatus(TaskStatus.DONE);
		taskRepository.save(task);

		return ResponseEntity.ok(null);
	}

	private List<TaskDto> getTasksByStatus(final Group group, final List<TaskStatus> statuses, final User excludedAssignee)
	{
		Stream<AssignedTask> taskStream = taskRepository.findAllByDefinitionGroupAndStatusIn(group, statuses).stream();

		if (excludedAssignee != null)
		{
			taskStream = taskStream.filter(assignedTask -> !assignedTask.getAssignee().equals(excludedAssignee));
		}

		return taskStream.sorted(Comparator.comparing(AssignedTask::getDueDate))
			.map(taskFactory::fromEntity)
			.collect(Collectors.toList());
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
