package giphouse.nl.proprapp.service.group.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haye
 */
@AllArgsConstructor
@Getter
@Setter
public class GroupListItemDto {

	private String groupName;

	private String admin;

	private List<String> usernames;
}
