package nl.giphouse.propr.controller;

import javax.inject.Inject;

import nl.giphouse.propr.repository.GroupRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		return ResponseEntity.ok("ok");
	}
}
