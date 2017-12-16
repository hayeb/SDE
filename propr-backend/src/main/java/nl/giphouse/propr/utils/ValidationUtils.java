package nl.giphouse.propr.utils;

import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author haye.
 */
public class ValidationUtils {

	public static boolean isValidJPEG(final @NonNull byte[] image)
	{
		return ArrayUtils.isEmpty(image)
			|| image.length >= 4 && image[0] == -1 && image[1] == -40 && image[image.length - 2] == -1 && image[image.length - 1] == -39;
	}
}
