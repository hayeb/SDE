package giphouse.nl.proprapp.ui.group;

import android.app.ListActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
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

public class GroupJoinActivity extends ListActivity {

	private static final String TAG = "GroupJoinActivity";

	@Inject
	GroupService groupService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication)getApplication()).getComponent().inject(this);

		setContentView(R.layout.activity_group_join);

		final SearchView groupSearchView = findViewById(R.id.search_group);
		groupSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(final String query) {
				searchGroups(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(final String newText) {
				return false;
			}
		});

		setListAdapter(new GroupSearchAdapter(getLayoutInflater(), this));
	}

	private void searchGroups(final String query) {
		Log.i(TAG, "Searching groups for \"" + query + "\"");
		groupService.searchGroups(query).enqueue(new Callback<List<GroupSearchResult>>() {
			@Override
			public void onResponse(@NonNull final Call<List<GroupSearchResult>> call, @NonNull final Response<List<GroupSearchResult>> response) {
				if (response.isSuccessful()) {
					((GroupSearchAdapter)getListAdapter()).updateEntries(response.body());
				}
			}

			@Override
			public void onFailure(@NonNull final Call<List<GroupSearchResult>> call, @NonNull final Throwable t) {
				t.printStackTrace();
			}
		});
	}
}
