package giphouse.nl.proprapp.dagger;

import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * @author haye
 */
public interface PicassoWrapper {

	void loadGroupImage(@NonNull Long groupId, @NonNull ImageView imageView);

	void invalidateGroupImage(@NonNull Long groupId);

	void loadGroupListImage(@NonNull Long groupId, @NonNull ImageView imageView);

	void loadTaskImage(@NonNull Long taskId, @NonNull ImageView imageView);
}
