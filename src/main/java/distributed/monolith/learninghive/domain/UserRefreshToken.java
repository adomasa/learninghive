package distributed.monolith.learninghive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false, unique = true)
	@NonNull
	private String token;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, unique = true)
	@JsonIgnore
	@NonNull
	private User user;

}