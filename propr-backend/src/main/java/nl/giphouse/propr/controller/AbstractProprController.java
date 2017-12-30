package nl.giphouse.propr.controller;

import lombok.NonNull;
import nl.giphouse.propr.controller.exception.GroupNotAuthorizedException;
import nl.giphouse.propr.controller.exception.GroupNotFoundException;
import nl.giphouse.propr.model.group.Group;
import nl.giphouse.propr.model.user.User;

/**
 * @author haye.
 */
public abstract class AbstractProprController {

	protected void checkAuthorized(final Group group, final @NonNull User user)
	{
		if (group == null)
		{
			throw new GroupNotFoundException();
		}

		if (!group.getUsers().contains(user))
		{
			throw new GroupNotAuthorizedException(group.getName(), user.getUsername());
		}
	}
}