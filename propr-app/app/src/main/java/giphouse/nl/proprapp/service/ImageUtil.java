package giphouse.nl.proprapp.service;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;

/**
 * @author haye
 */
public class ImageUtil {

	public static MediaType JPEG_TYPE = MediaType.parse("image/jpeg");

	private static Bitmap resizeBitmap(final Bitmap bitmap, final int newWidth) {
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();

		if (newWidth >= width) {
			return bitmap;
		}

		final float scaleWidth = ((float) newWidth) / width;
		final Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);

		final Bitmap resizedBitmap = Bitmap.createBitmap(
			bitmap, 0, 0, width, height, matrix, false);
		bitmap.recycle();
		return resizedBitmap;
	}

	/**
	 * Creates a byte array from a BitmapDrawable and reduces the size, keeping the aspect ratio. If the image is already smaller than the specified width, it is not resized.
	 */
	public static byte[] getImageBytes(final @NonNull BitmapDrawable drawable, final int width) {
		return getImageBytes(drawable.getBitmap(), width);
	}

	/**
	 * Creates a byte array from a Bitmap and reduces the size, keeping the aspect ratio. If the image is already smaller than the specified width, it is not resized.
	 * The
	 */
	public static byte[] getImageBytes(final @NonNull Bitmap bitmap, final int width) {
		final Bitmap resized = resizeBitmap(bitmap, width);
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		resized.compress(Bitmap.CompressFormat.JPEG, 50, stream);

		return stream.toByteArray();
	}
}
