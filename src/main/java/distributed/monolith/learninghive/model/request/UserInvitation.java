package distributed.monolith.learninghive.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserInvitation extends VersionedResourceRequest {
	@Email
	String email;
}
