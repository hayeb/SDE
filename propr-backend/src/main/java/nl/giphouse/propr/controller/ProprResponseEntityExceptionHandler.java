package nl.giphouse.propr.controller;

import nl.giphouse.propr.controller.exception.GroupNotAuthorizedException;
import nl.giphouse.propr.controller.exception.GroupNotFoundException;
import nl.giphouse.propr.controller.exception.TaskDefinitionInvalidException;
import nl.giphouse.propr.controller.exception.TaskRatingInvalidException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author haye.
 */
@RestControllerAdvice("nl.giphouse.propr.controller")
public class ProprResponseEntityExceptionHandler extends ResponseEntityExceptionHandler
{
	@ExceptionHandler(GroupNotAuthorizedException.class)
	protected ResponseEntity<Object> handleGroupNotAuthorized(final GroupNotAuthorizedException exc, final WebRequest webRequest)
	{
		return new ResponseEntity<>("User " + exc.getUsername() + " is not in " + exc.getGroupName(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(GroupNotFoundException.class)
	protected ResponseEntity<Object> handleGroupNotFound(final GroupNotFoundException exc, final WebRequest webRequest)
	{
		return new ResponseEntity<>("Group not found", new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(TaskDefinitionInvalidException.class)
	protected ResponseEntity<Object> handleTaskDefinitionInvalid(final TaskDefinitionInvalidException exc, final WebRequest webRequest)
	{
		return new ResponseEntity<>("Task definition invalid: " + exc.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TaskRatingInvalidException.class)
	protected ResponseEntity<Object> handleTaskRatingInvalid(final TaskRatingInvalidException exc, final WebRequest webRequest)
	{
		return new ResponseEntity<>("Task rating invalid: " + exc.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}
}
