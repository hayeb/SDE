package giphouse.nl.proprapp.ui.group.overview;

import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskDto;

/**
 * @author haye
 */
public interface OnGroupTasksFragmentInteractionListener {

	void onGroupActivityFragmentInteraction(TaskDto item);

	void onGroupScheduleFragmentInteraction(TaskDto item);
}
