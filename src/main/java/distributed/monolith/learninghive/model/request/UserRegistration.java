package distributed.monolith.learninghive.model.request;


import distributed.monolith.learninghive.model.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class UserRegistration {
	@Email
	private final String email;

	@NotBlank
	@ValidPassword
	private final String password;

	@NotBlank
	private final String name;

	@NotBlank
	private final String surname;
}
