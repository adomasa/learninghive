package distributed.monolith.learninghive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false, unique = true)
	@NonNull
	@Length(max = 254, message = "Email is too long")
	private String email;

	@JsonIgnore
	@Column(nullable = false)
	@Length(max = 254, message = "Password is too long")
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

}