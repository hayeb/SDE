package giphouse.nl.proprapp.service.group.search;

/**
 * @author haye
 */
public class GroupSearchResult {

	public String getGroupName() {
		return groupName;
	}

	public byte[] getImage() {
		return image;
	}

	private final String groupName;

	private final byte[] image;

	public GroupSearchResult(final String groupName, final byte[] image) {
		this.groupName = groupName;
		this.image = image;
	}
}
