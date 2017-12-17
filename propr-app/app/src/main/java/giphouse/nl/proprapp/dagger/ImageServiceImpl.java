package giphouse.nl.proprapp.dagger;

import android.support.annotation.NonNull;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.Locale;

import giphouse.nl.proprapp.ProprConfiguration;

/**
 * @author haye
 */
public class ImageServiceImpl implements ImageService {

	private static final String URL_GROUP_IMAGE_FORMAT = "api/group/%d/image";

	private static final String URL_USER_AVATAR = "api/user/%d/avatar";

	private final Picasso picasso;

	private final ProprConfiguration proprConfiguration;

	ImageServiceImpl(final Picasso picasso, final ProprConfiguration proprConfiguration) {
		this.picasso = picasso;
		this.proprConfiguration = proprConfiguration;
	}

	@Override
	public RequestCreator loadGroupImage(final @NonNull Long groupId) {

		return picasso.load(proprConfiguration.getBackendUrl() + String.format(Locale.ENGLISH, URL_GROUP_IMAGE_FORMAT, groupId));
	}

	@Override
	public void invalidateGroupImage(final @NonNull Long groupId) {
		picasso.invalidate(proprConfiguration.getBackendUrl() + String.format(Locale.ENGLISH, URL_GROUP_IMAGE_FORMAT, groupId));
	}

	@Override
	public RequestCreator loadTaskImage(final @NonNull Long taskId) {
		return picasso.load(proprConfiguration.getBackendUrl() + "api/task/" + taskId + "/image");
	}

	@Override
	public RequestCreator loadAccountAvatar(final @NonNull Long userId) {
		return picasso.load(proprConfiguration.getBackendUrl() + String.format(Locale.ENGLISH, URL_USER_AVATAR, userId));
	}

	@Override
	public void invalidateAccountAvatar(final @NonNull Long userId) {
		picasso.invalidate(proprConfiguration.getBackendUrl() + String.format(Locale.ENGLISH, URL_USER_AVATAR, userId));
	}
}
