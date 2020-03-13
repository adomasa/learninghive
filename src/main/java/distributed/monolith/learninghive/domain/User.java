package distributed.monolith.learninghive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(nullable = false, unique = true)
	@NonNull
	private String email;

	@JsonIgnore
	@Column(nullable = false)
	@NonNull
	private String password;

	@Column(nullable = false)
	@NonNull
	private String name;

	@Column(nullable = false)
	@NonNull
	private String surname;

	@ElementCollection(fetch = FetchType.EAGER)
	@NonNull
	private List<Role> roles;

	@ManyToOne
	private User supervisor;

	@OneToMany(mappedBy = "supervisor")
	private List<User> subordinates;

	protected User() {
	}
}
