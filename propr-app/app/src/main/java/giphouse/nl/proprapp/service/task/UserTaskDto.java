package giphouse.nl.proprapp.service.task;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author haye
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserTaskDto {

	private String name;

	private String description;

	private Long assigneeId;

	private Long groupId;

	private LocalDate dueDate;
}
