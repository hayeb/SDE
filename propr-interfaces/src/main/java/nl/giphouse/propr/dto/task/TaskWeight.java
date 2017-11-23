package nl.giphouse.propr.dto.task;

/**
 * @author haye.
 */
public enum TaskWeight {
	LIGHT,
	MEDIUM,
	HEAVY;

	public int getValue()
	{
		return ordinal() + 1;
	}
}
