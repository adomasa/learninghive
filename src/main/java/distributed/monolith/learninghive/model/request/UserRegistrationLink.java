package distributed.monolith.learninghive.model.request;

import distributed.monolith.learninghive.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import javax.validation.constraints.Email;

@Getter
@AllArgsConstructor
public class UserRegistrationLink {
    @Email
    @NonNull
    private final String email;

    @NonNull
    private final long userWhoInvitedId;
}
