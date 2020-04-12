package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Invitation;
import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.InvalidTokenException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.response.UserInfo;
import distributed.monolith.learninghive.repository.InvitationRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	public static final int INVITATION_TOKEN_SIZE = 32;

	private final InvitationRepository invitationRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${website.url}")
	private String websiteUrl;

	@Value("${website.registrationPath}")
	private String registrationPath;

	public void delete(String email) {
		userRepository.deleteByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email)
				);
	}

	@Transactional
	public User registerUser(String invitationToken, UserRegistration userRegistration, List<Role> roles) {
		var invitation = invitationRepository.findByValidationToken(invitationToken)
				.orElseThrow(() -> new InvalidTokenException("invitation", invitationToken));

		var user = User.builder()
				.email(invitation.getEmail())
				.password(passwordEncoder.encode(userRegistration.getPassword()))
				.name(userRegistration.getName())
				.surname(userRegistration.getSurname())
				.roles(roles)
				.supervisor(invitation.getUserWhoInvited())
				.build();

		user = userRepository.save(user);
		invitation.getUserWhoInvited().getSubordinates().add(user);
		invitationRepository.delete(invitation);
		return user;
	}

	public String createInvitationLink(UserInvitation userInvitation, long userId) {
		if (userRepository.findByEmail(userInvitation.getEmail()).isPresent()) {
			throw new DuplicateResourceException(User.class.getSimpleName(), "email", userInvitation.getEmail());
		}

		if (invitationRepository.findByEmail(userInvitation.getEmail()).isPresent()) {
			throw new DuplicateResourceException(Invitation.class.getSimpleName(), "email", userInvitation.getEmail());
		}

		var userWhoInvited = userRepository
				.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), userId));

		var invitation = invitationRepository.save(
				new Invitation(
						userInvitation.getEmail(),
						RandomStringUtils.randomAlphanumeric(INVITATION_TOKEN_SIZE),
						userWhoInvited
				)
		);

		return UriComponentsBuilder.newInstance()
				.scheme("http")
				.host(websiteUrl)
				.path(registrationPath)
				.queryParam("token", invitation.getValidationToken()).build()
				.toUriString();
	}

	public UserInfo getUserInfo(Long userId) {
		return userRepository.findById(userId)
				.map(user -> new UserInfo(user.getEmail(), user.getName()))
				.orElseThrow(IllegalStateException::new);
	}

}
