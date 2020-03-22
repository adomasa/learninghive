package distributed.monolith.learninghive.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import javax.validation.constraints.Email;

@Getter
@AllArgsConstructor
public class UserInvitation {
    @Email
    private final String email;
}
