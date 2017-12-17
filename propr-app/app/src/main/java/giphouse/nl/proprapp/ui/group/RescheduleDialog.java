package giphouse.nl.proprapp.ui.group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import nl.giphouse.propr.dto.group.GenerateScheduleDto;
import nl.giphouse.propr.dto.task.TaskRepetitionType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haye
 */
public class RescheduleDialog extends DialogFragment {
	public static String ARG_GROUP_ID = "groupId";

	@Inject
	GroupService groupService;

	private RescheduleDialogListener listener;
	private Long groupId;

	private TextInputEditText aantalText;
	private Spinner spinner;

	public static RescheduleDialog newInstance(final Long groupId) {
		final RescheduleDialog fragment = new RescheduleDialog();
		final Bundle args = new Bundle();
		args.putLong(ARG_GROUP_ID, groupId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(final @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getActivity().getApplication()).getComponent().inject(this);

		if (getArguments() != null) {
			groupId = getArguments().getLong(ARG_GROUP_ID);
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

		final LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_reschedule, null);

		dialogBuilder.setView(view);

		aantalText = view.findViewById(R.id.aantal);
		spinner = view.findViewById(R.id.type);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter
			.createFromResource(this.getActivity(), R.array.frequencytypes,
				android.R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);

		dialogBuilder.setPositiveButton(R.string.reschedule, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				final int amount = Integer.parseInt(aantalText.getText().toString());
				final TaskRepetitionType type = TaskRepetitionType.valueOf(String.valueOf(spinner.getSelectedItem()));
				groupService.rescheduleGroup(groupId, new GenerateScheduleDto(numberOfDays(amount, type)))
					.enqueue(new Callback<Void>() {
						@Override
						public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
							if (response.isSuccessful()) {
								listener.onReschedule();
							}
						}

						@Override
						public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {

						}
					});
			}
		}).setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				RescheduleDialog.this.getDialog().cancel();
				listener.onCancel();
			}
		});

		return dialogBuilder.create();
	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);

		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			listener = (RescheduleDialogListener) context;
		} catch (final ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
		}
	}

	private int numberOfDays(final int amount, final TaskRepetitionType type) {
		switch (type) {
			case DAY:
				return amount;
			case WEEK:
				return amount * 7;
			case MONTH:
				return amount * 31;
			case YEAR:
				return amount * 365;
		}
		return 0;
	}

	public interface RescheduleDialogListener {
		void onReschedule();
		void onCancel();
	}
}