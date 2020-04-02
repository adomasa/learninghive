package distributed.monolith.learninghive.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Invitation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false, unique = true)
	@NonNull
	@Length(max = 254, message = "Email is too long")
	private String email;

	@Column(nullable = false)
	@CreationTimestamp
	private Date date;

	@Column(nullable = false, unique = true)
	@NonNull
	private String validationToken;

	@NonNull
	@ManyToOne
	private User userWhoInvited;
}