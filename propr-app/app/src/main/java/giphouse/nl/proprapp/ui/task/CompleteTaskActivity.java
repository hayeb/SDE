package giphouse.nl.proprapp.ui.task;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.ImageUtil;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskCompletionDto;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haye
 */
public class CompleteTaskActivity extends AppCompatActivity {

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int PERMISSIONS_REQUEST_CAMERA = 12;

	private static final String TAG = "CompleteTaskActivity";

	public static String ARG_TASK_ID = "taskId";
	public static String ARG_TASK_NAME = "taskName";
	public static String ARG_TASK_DESCRIPTION = "taskDescription";

	private static String ARG_PHOTO_PATH = "photoPath";

	@Inject
	TaskService taskService;

	@Inject
	Picasso picasso;

	private String taskName;
	private String taskDescription;
	private Long taskId;

	private TextInputEditText notesField;
	private ImageView imageView;
	private Button takePictureButton;

	/**
	 * Filename of the photo file created by the camera app
	 */
	private String mCurrentPhotoPath;

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		final Bundle arguments = getIntent().getExtras();
		if (savedInstanceState != null) {
			taskName = savedInstanceState.getString(ARG_TASK_NAME);
			taskId = savedInstanceState.getLong(ARG_TASK_ID);
			taskDescription = savedInstanceState.getString(ARG_TASK_DESCRIPTION);
			mCurrentPhotoPath = savedInstanceState.getString(ARG_PHOTO_PATH);
		} else if (arguments != null) {
			taskName = arguments.getString(ARG_TASK_NAME);
			taskDescription = arguments.getString(ARG_TASK_DESCRIPTION);
			taskId = arguments.getLong(ARG_TASK_ID);
		}

		setContentView(R.layout.activity_task_complete);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("Complete \'" + taskName + "\'");

		imageView = findViewById(R.id.task_complete_image);

		if (mCurrentPhotoPath != null)
		{
			imageView.setVisibility(View.VISIBLE);
			picasso.load(new File(mCurrentPhotoPath)).into(imageView);
		}
		notesField = findViewById(R.id.task_completion_notes);
		takePictureButton = findViewById(R.id.take_picture_button);

		final TextView descriptionText = findViewById(R.id.task_description);
		descriptionText.setText(taskDescription);

		takePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final int permissionCheck = ContextCompat.checkSelfPermission(CompleteTaskActivity.this,
					Manifest.permission.CAMERA);

				if (permissionCheck == PackageManager.PERMISSION_DENIED) {
					ActivityCompat.requestPermissions(CompleteTaskActivity.this,
						new String[]{Manifest.permission.CAMERA},
						PERMISSIONS_REQUEST_CAMERA);
				} else {
					takePicture();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Log.d(TAG, "Picture taken");
			imageView.setVisibility(View.VISIBLE);

			picasso.load(new File(mCurrentPhotoPath)).into(imageView);
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		final int id = item.getItemId();
		if (id == android.R.id.home) {
			navigateToParent();
		} else if (id == R.id.complete_task) {
			completeTask();
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_REQUEST_CAMERA: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					takePicture();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.activity_task_complete, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putLong(ARG_TASK_ID, taskId);
		outState.putString(ARG_TASK_NAME, taskName);
		outState.putString(ARG_TASK_DESCRIPTION, taskDescription);
		outState.putString(ARG_PHOTO_PATH, mCurrentPhotoPath);
		super.onSaveInstanceState(outState);
	}

	private void completeTask() {
		final TaskCompletionDto dto = TaskCompletionDto.builder()
			.taskCompletionDescription(notesField.getText().toString())
			.build();

		taskService.completeTask(taskId, dto).enqueue(new Callback<Void>() {
			@Override
			public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
				if (response.isSuccessful()) {
					uploadImage();
					navigateToParent();
					Toast.makeText(CompleteTaskActivity.this, "Task completed! Well done!", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(CompleteTaskActivity.this, "Unable to complete task", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
				Toast.makeText(CompleteTaskActivity.this, "Error connecting to server", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void uploadImage() {
		if (imageView.getDrawable() == null) {
			return;
		}
		final byte[] image = ImageUtil.getImageBytes((BitmapDrawable) imageView.getDrawable(), 1500);
		final RequestBody body = RequestBody.create(ImageUtil.JPEG_TYPE, image);
		taskService.uploadImage(taskId, body).enqueue(new Callback<Void>() {
			@Override
			public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {

			}

			@Override
			public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {

			}
		});
	}

	private void navigateToParent() {
		final Intent intent = NavUtils.getParentActivityIntent(this);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
			| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		NavUtils.navigateUpTo(this, intent);
	}

	private void takePicture() {
		// TODO: Remove file when taking a new picture
		final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
			return;
		}
		final File photoFile;
		try {
			photoFile = createImageFile();
		} catch (final IOException ex) {
			return;
		}
		final Uri photoURI = FileProvider.getUriForFile(CompleteTaskActivity.this,
			"nl.giphouse.propr.fileprovider",
			photoFile);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
		startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		final String timeStamp =new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		final String imageFileName = "JPEG_" + timeStamp + "_";
		final File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		final File tempFile = File.createTempFile(
			imageFileName,  /* prefix */
			".jpg",         /* suffix */
			storageDir      /* directory */
		);
		mCurrentPhotoPath = tempFile.getAbsolutePath();

		return tempFile;
	}
}
