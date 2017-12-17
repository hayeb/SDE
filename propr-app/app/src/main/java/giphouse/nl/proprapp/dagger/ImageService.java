package giphouse.nl.proprapp.dagger;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.RequestCreator;

/**
 * @author haye
 */
public interface ImageService {

	RequestCreator loadGroupImage(@NonNull Long groupId);

	void invalidateGroupImage(@NonNull Long groupId);

	RequestCreator loadTaskImage(@NonNull Long taskId);

	RequestCreator loadAccountAvatar(@NonNull Long userId);

	void invalidateAccountAvatar(@NonNull Long userId);

}