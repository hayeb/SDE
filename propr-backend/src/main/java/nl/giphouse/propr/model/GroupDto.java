package nl.giphouse.propr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author haye.
 */
@AllArgsConstructor
@Getter
public class GroupDto {

	private String groupName;

	private String admin;

	private List<String> usernames;
}
