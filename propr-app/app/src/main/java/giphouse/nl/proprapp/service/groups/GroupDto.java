package giphouse.nl.proprapp.service.groups;

import java.util.List;

/**
 * @author haye
 */

public class GroupDto {

	private String groupName;

	private String admin;

	private List<String> usernames;

	public GroupDto(final String groupName, final String adminName, final List<String> usernames) {
		this.groupName = groupName;
		this.admin = adminName;
		this.usernames = usernames;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(final String admin) {
		this.admin = admin;
	}

	public List<String> getUsernames() {
		return usernames;
	}

	public void setUsernames(final List<String> usernames) {
		this.usernames = usernames;
	}
}
