package giphouse.nl.proprapp.ui.group;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SearchView;

import java.util.List;

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

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		setContentView(R.layout.activity_group_join);

	}

}
