package nl.giphouse.propr.controller;

import nl.giphouse.propr.repository.GroupRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * @author haye.
 */
@RestController
@RequestMapping("/api/group")
public class GroupController {

	@Inject
	private GroupRepository groupRepository;

	@RequestMapping("/create")
	public ResponseEntity createGroup(){

	}
}
