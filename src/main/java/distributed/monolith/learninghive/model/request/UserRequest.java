package distributed.monolith.learninghive.model.request;

import distributed.monolith.learninghive.model.validation.ValidPassword;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@EqualsAndHashCode(callSuper = true)
public class UserRequest extends VersionedResourceRequest {
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
