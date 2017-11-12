package giphouse.nl.proprapp.service.group.model;

import android.os.Parcel;
import android.os.Parcelable;

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
public class GroupListItemDto implements Parcelable {

	private String groupName;

	private String admin;

	private List<String> usernames;

	private GroupListItemDto(Parcel in)
	{
		groupName = in.readString();
		admin = in.readString();
		usernames = in.createStringArrayList();
	}

	public static final Creator<GroupListItemDto> CREATOR = new Creator<GroupListItemDto>() {
		@Override
		public GroupListItemDto createFromParcel(Parcel in) {
			return new GroupListItemDto(in);
		}

		@Override
		public GroupListItemDto[] newArray(int size) {
			return new GroupListItemDto[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(groupName);
		dest.writeString(admin);
		dest.writeStringList(usernames);
	}
}
