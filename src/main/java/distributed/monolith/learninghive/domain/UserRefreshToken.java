package distributed.monolith.learninghive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class UserRefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false, unique = true)
	private String token;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, unique = true)
	@JsonIgnore
	private User user;

	public UserRefreshToken() {
	}

	public UserRefreshToken(String token, User user) {
		this.token = token;
		this.user = user;
	}

}
