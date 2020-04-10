package distributed.monolith.learninghive.model.request;

import distributed.monolith.learninghive.model.validation.ValidPassword;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserRequest {
	@Email
	String email;

	@NotBlank
	@ValidPassword
	String password;

	@NotBlank
	String name;

	@NotBlank
	String surname;
}
