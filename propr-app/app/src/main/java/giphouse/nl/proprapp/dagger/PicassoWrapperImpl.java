package giphouse.nl.proprapp.dagger;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import giphouse.nl.proprapp.ProprConfiguration;

/**
 * @author haye
 */
public class PicassoWrapperImpl implements PicassoWrapper {

	private static final String URL_GROUP_IMAGE_FORMAT = "api/group/%d/image";

	private final Picasso picasso;

	private final ProprConfiguration proprConfiguration;

	PicassoWrapperImpl(final Picasso picasso, final ProprConfiguration proprConfiguration) {
		this.picasso = picasso;
		this.proprConfiguration = proprConfiguration;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void loadGroupImage(final @NonNull Long groupId, final @NonNull ImageView imageView) {

		picasso.load(proprConfiguration.getBackendUrl() + String.format(URL_GROUP_IMAGE_FORMAT, groupId)).into(imageView);
	}

	@Override
	public void invalidateGroupImage(final @NonNull Long groupId) {
		picasso.invalidate(proprConfiguration.getBackendUrl() + String.format(URL_GROUP_IMAGE_FORMAT, groupId));
	}

	@Override
	public void loadGroupListImage(final @NonNull Long groupId, final @NonNull ImageView imageView) {
		picasso.load(proprConfiguration.getBackendUrl() + "api/group/" + groupId + "/image").into(imageView);
	}

	@Override
	public void loadTaskImage(final @NonNull Long taskId, final @NonNull ImageView imageView) {
		picasso.load(proprConfiguration.getBackendUrl() + "api/task/" + taskId + "/image").into(imageView);
	}
}
