package distributed.monolith.learninghive.model.request;

import lombok.Value;

import javax.validation.constraints.Email;

@Value
public class UserInvitation {
	@Email
	String email;
}
