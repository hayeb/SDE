package nl.giphouse.propr.controller;

import nl.giphouse.propr.controller.exception.GroupNotAuthorizedException;

import nl.giphouse.propr.controller.exception.GroupNotFoundException;
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
@RestControllerAdvice
public class ProprResponseEntityExceptionHandler extends ResponseEntityExceptionHandler
{
	@ExceptionHandler(GroupNotAuthorizedException.class)
	protected ResponseEntity<Object> handleGroupNotAuthorized(final GroupNotAuthorizedException exc, final WebRequest webRequest)
	{
		return new ResponseEntity<>("User " + exc.getUsername()+ " is not in " + exc.getGroupName(),new HttpHeaders(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(GroupNotFoundException.class)
	protected ResponseEntity<Object> handleGroupNotFound(final GroupNotFoundException exc, final WebRequest webRequest)
	{
		return new ResponseEntity<>("Group not found",new HttpHeaders(), HttpStatus.NOT_FOUND);
	}
}
