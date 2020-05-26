package distributed.monolith.learninghive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false, unique = true)
	@Length(max = 254, message = "Email is too long")
	private String email;

	@JsonIgnore
	@Column(nullable = false)
	@Length(max = 254, message = "Password is too long")
	private String password;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String surname;

	@Column(nullable = false)
	private Role role;

	@ManyToOne
	private User supervisor;

	@OneToMany(mappedBy = "supervisor")
	@Builder.Default
	private List<User> subordinates = new ArrayList<>();

}