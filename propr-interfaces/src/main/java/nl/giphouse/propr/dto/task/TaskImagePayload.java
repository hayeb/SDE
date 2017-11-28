package nl.giphouse.propr.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haye.
 */
@AllArgsConstructor
@Getter
@Setter
public class TaskImagePayload {

	final byte[] image;
}
