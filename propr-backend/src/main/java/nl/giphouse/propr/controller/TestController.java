package nl.giphouse.propr.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haye.
 */
@RestController
@RequestMapping("/api/test")
public class TestController
{
	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	public ResponseEntity helloWorld(final Principal principal)
	{
		return ResponseEntity.ok("Hello " + principal.getName());
	}
}
