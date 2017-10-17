package nl.giphouse.propr.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author haye.
 */
@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Group {

	public static final String PROPERTY_ID = "id";

	private static final long serialVersionUID = 0L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String name;

	@Column
	private String inviteCode;

	@ManyToOne
	@JoinColumn(name = "admin")
	private User admin;

	@OneToMany
	@JoinTable(name = "users_groups",
			joinColumns = {@JoinColumn(name = "group_id")},
			inverseJoinColumns = {@JoinColumn(name = "user_id")})
	private List<User> users;

	public Group(final String name, final String inviteCode, final User admin, final List<User> users) {
		this.name = name;
		this.inviteCode = inviteCode;
		this.admin = admin;
		this.users = users;
	}

}
