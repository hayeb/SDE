package giphouse.nl.proprapp.dagger;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import giphouse.nl.proprapp.ProprConfiguration;

/**
 * @author haye
 */
public class ImageServiceImpl implements ImageService {

	private static final String URL_GROUP_IMAGE_FORMAT = "api/group/%d/image";

	private final Picasso picasso;

	private final ProprConfiguration proprConfiguration;

	ImageServiceImpl(final Picasso picasso, final ProprConfiguration proprConfiguration) {
		this.picasso = picasso;
		this.proprConfiguration = proprConfiguration;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public RequestCreator loadGroupImage(final @NonNull Long groupId) {

		return picasso.load(proprConfiguration.getBackendUrl() + String.format(URL_GROUP_IMAGE_FORMAT, groupId));
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void invalidateGroupImage(final @NonNull Long groupId) {
		picasso.invalidate(proprConfiguration.getBackendUrl() + String.format(URL_GROUP_IMAGE_FORMAT, groupId));
	}

	@Override
	public RequestCreator loadTaskImage(final @NonNull Long taskId) {
		return picasso.load(proprConfiguration.getBackendUrl() + "api/task/" + taskId + "/image");
	}
}
