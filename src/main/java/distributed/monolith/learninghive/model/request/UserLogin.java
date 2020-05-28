package distributed.monolith.learninghive.model.request;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class UserLogin {
	@NotBlank
	String email;

	@NotBlank
	String password;
}
