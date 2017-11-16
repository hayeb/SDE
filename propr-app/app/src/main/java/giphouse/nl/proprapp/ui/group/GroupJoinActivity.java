package giphouse.nl.proprapp.ui.group;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.service.group.search.GroupSearchAdapter;
import giphouse.nl.proprapp.service.group.search.GroupSearchResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupJoinActivity extends AppCompatActivity {

	private static final String TAG = "GroupJoinActivity";

	@Inject
	GroupService groupService;

	private TextInputEditText enterGroupcode;
	private Button button;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ProprApplication) getApplication()).getComponent().inject(this);
		setContentView(R.layout.activity_group_join);

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Optional.ofNullable(getSupportActionBar()).ifPresent(bar -> bar.setDisplayHomeAsUpEnabled(true));

		enterGroupcode = findViewById(R.id.enterGroupcode);
		button = findViewById(R.id.button);

	}

}
