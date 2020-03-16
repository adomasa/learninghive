package distributed.monolith.learninghive.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class UserLogin {
	private final String email;

	@NotBlank
	private final String password;
}
