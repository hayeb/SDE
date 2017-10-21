package giphouse.nl.proprapp.ui.groups;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageButton;

import giphouse.nl.proprapp.R;

public class GroupAddActivity extends AppCompatActivity {

	private TextInputEditText groupNameEdit;
	private TextInputEditText groupCodeEdit;
	private ImageButton groupImageButton;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_add);
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		groupNameEdit = findViewById(R.id.editGroupname);
		groupCodeEdit = findViewById(R.id.editGroupcode);
		groupImageButton = findViewById(R.id.groupImageButton);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.menu_group_add, menu);

		return super.onCreateOptionsMenu(menu);
	}
}
