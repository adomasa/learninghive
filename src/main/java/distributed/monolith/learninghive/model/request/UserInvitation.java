package distributed.monolith.learninghive.model.request;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserInvitation {
	@Email
	String email;
}
