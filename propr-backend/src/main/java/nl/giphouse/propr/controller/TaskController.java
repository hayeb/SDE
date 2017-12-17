package nl.giphouse.propr.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import nl.giphouse.propr.dto.task.TaskCompletionDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskRatingDto;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.task.AssignedTask;
import nl.giphouse.propr.model.task.CompletedTask;
import nl.giphouse.propr.model.task.TaskDefinition;
import nl.giphouse.propr.model.task.TaskFactory;
import nl.giphouse.propr.model.task.TaskRating;
import nl.giphouse.propr.model.user.User;
import nl.giphouse.propr.repository.CompletedTaskRepository;
import nl.giphouse.propr.repository.GroupRepository;
import nl.giphouse.propr.repository.TaskDefinitionRepository;
import nl.giphouse.propr.repository.TaskRatingRepository;
import nl.giphouse.propr.repository.TaskRepository;
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
	private TaskRatingRepository taskRatingRepository;

	@Inject
	private TaskDefinitionRepository taskDefinitionRepository;

	@Inject
	private CompletedTaskRepository completedTaskRepository;

	@Inject
	private GroupRepository groupRepository;

	@Inject
	private UserService userService;

	@Inject
	private TaskFactory taskFactory;

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

		return ResponseEntity.ok(taskRepository.findTasksForUserInGroup(group, user).stream()
			.map(taskFactory::toTaskDto)
			.collect(Collectors.toList()));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/group/activity")
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

		return ResponseEntity.ok(taskRepository.findActivityInGroup(group).stream()
			.map(taskFactory::toTaskDto)
			.collect(Collectors.toList()));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/group/scheduled")
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

		return ResponseEntity.ok(taskRepository.findTodoTasksInGroup(group).stream()
			.map(taskFactory::toTaskDto)
			.collect(Collectors.toList()));
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
			.map(taskFactory::toTaskDefinitionDto)
			.collect(Collectors.toList());

		return ResponseEntity.ok(definitions);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/group/add")
	public ResponseEntity<TaskDefinitionDto> addTaskToGroup(@RequestBody final TaskDefinitionDto taskDefinitionDto, final Principal principal)
	{
		final User user = (User) userService.loadUserByUsername(principal.getName());

		final Group group = groupRepository.findGroupById(taskDefinitionDto.getGroupId());

		if (group == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		if (!group.getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		log.debug("Handling /api/task/group/add");

		final TaskDefinition taskDef = taskFactory.fromTaskDefitionDto(taskDefinitionDto);
		taskDefinitionRepository.save(taskDef);

		return ResponseEntity.ok(taskFactory.toTaskDefinitionDto(taskDef));
	}

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

		if (task.getCompletedTask() != null)
		{
			return ResponseEntity.badRequest().build();
		}

		final CompletedTask completedTask = new CompletedTask();
		completedTask.setDate(LocalDate.now());
		completedTask.setDescription(taskCompletionDto.getTaskCompletionDescription());

		completedTaskRepository.save(completedTask);

		task.setCompletedTask(completedTask);
		taskRepository.save(task);

		return ResponseEntity.ok(null);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{taskId}/image")
	public ResponseEntity<Void> uploadImageForTask(final Principal principal, final @PathVariable long taskId, final @RequestBody byte[] image)
	{
		if (!ValidationUtils.isValidJPEG(image))
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		final AssignedTask task = taskRepository.findOne(taskId);

		log.debug("Handling POST /api/task/{taskId}/image");

		if (task == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		final User user = (User) userService.loadUserByUsername(principal.getName());

		if (!task.getDefinition().getGroup().getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		if (task.getCompletedTask() == null)
		{
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
		}

		final CompletedTask completedTask = task.getCompletedTask();

		completedTask.setImage(image);
		completedTaskRepository.save(task.getCompletedTask());

		return ResponseEntity.ok(null);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{taskId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getImageForTask(final Principal principal, final @PathVariable long taskId)
	{
		final AssignedTask task = taskRepository.findOne(taskId);

		log.debug("Handling GET /api/task/{taskId}/image");

		if (task == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		final User user = (User) userService.loadUserByUsername(principal.getName());

		if (!task.getDefinition().getGroup().getUsers().contains(user))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		if (task.getCompletedTask() == null || task.getCompletedTask().getImage() == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		final byte[] image = task.getCompletedTask().getImage();
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		headers.setContentLength(image.length);

		return new ResponseEntity<>(image, headers, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{taskId}/rate")
	public ResponseEntity<?> rateTask(final Principal principal, final @PathVariable Long taskId, final @RequestBody TaskRatingDto taskRatingDto)
	{
		final AssignedTask task = taskRepository.findOne(taskId);

		log.debug("Handling POST /api/task/{taskId}/rate");

		if (task == null)
		{
			log.debug("No task with id {} found", taskId);
			return ResponseEntity.notFound().build();
		}

		final User user = (User) userService.loadUserByUsername(principal.getName());
		final Group group = task.getDefinition().getGroup();

		final ResponseEntity<?> authorizationResponse = checkAuthorized(user, group);
		if (authorizationResponse != null)
		{
			return authorizationResponse;
		}

		if (task.getCompletedTask() == null)
		{
			log.debug("Task {} is not yet completed", task.getId());
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}

		if (!validateRating(taskRatingDto))
		{
			log.debug("Input failed validation");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		final TaskRating taskRating = task.getCompletedTask().getRatings().stream()
			.filter(t -> t.getAuthor().equals(user))
			.findFirst().orElseGet(TaskRating::new);

		taskRating.setAuthor(user);
		taskRating.setComment(taskRatingDto.getComment());
		taskRating.setScore(taskRatingDto.getScore());
		taskRating.setCompletedTask(task.getCompletedTask());

		taskRatingRepository.save(taskRating);

		return ResponseEntity.ok().build();
	}

	@RequestMapping(method = RequestMethod.GET, value = "{taskId}/rate")
	public ResponseEntity<?> getTaskRatingForUser(final Principal principal, final @PathVariable long taskId)
	{
		final AssignedTask task = taskRepository.findOne(taskId);

		log.debug("Handling GET /api/task/{taskId}/rate");

		if (task == null)
		{
			log.debug("No task with id {} found", taskId);
			return ResponseEntity.notFound().build();
		}

		final User user = (User) userService.loadUserByUsername(principal.getName());
		final Group group = task.getDefinition().getGroup();

		final ResponseEntity<?> authorizationResponse = checkAuthorized(user, group);
		if (authorizationResponse != null)
		{
			return authorizationResponse;
		}

		if (task.getCompletedTask() == null)
		{
			log.debug("Task {} is not yet completed", task.getId());
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}

		final TaskRating rating = task.getCompletedTask().getRatings().stream()
			.filter(r -> r.getAuthor().equals(user))
			.findFirst().orElse(null);

		if (rating == null)
		{
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(new TaskRatingDto(rating.getScore(), rating.getComment()));
	}

	private boolean validateRating(final TaskRatingDto dto)
	{
		return dto.getScore() > 0 && dto.getScore() <= 10;
	}

	private ResponseEntity<?> checkAuthorized(final User user, final Group group)
	{
		if (group == null)
		{
			log.debug("No group found");
			return ResponseEntity.notFound().build();
		}

		if (!group.getUsers().contains(user))
		{
			log.debug("User {} not authorized for group {}", user.getUsername(), group.getName());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return null;
	}
}
