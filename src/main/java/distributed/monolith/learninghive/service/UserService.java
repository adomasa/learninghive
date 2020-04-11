package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Invitation;
import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateEmailException;
import distributed.monolith.learninghive.model.exception.InvalidInvitationTokenException;
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

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	private final InvitationRepository invitationRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${server.port}")
	private String port;
	@Value("${server.domain}")
	private String domain;

	public void delete(String email) {
		userRepository.deleteByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email)
				);
	}

	@Transactional
	public User registerUser(String invitationToken, UserRegistration userRegistration, List<Role> roles) {
		Invitation invitation = invitationRepository.findByValidationToken(invitationToken)
				.orElseThrow(() -> new InvalidInvitationTokenException(invitationToken));

		User user = User.builder()
				.email(invitation.getEmail())
				.password(passwordEncoder.encode(userRegistration.getPassword()))
				.name(userRegistration.getName())
				.surname(userRegistration.getSurname())
				.roles(roles)
				.supervisor(invitation.getUserWhoInvited())
				.build();

		user = userRepository.save(user);
		invitation.getUserWhoInvited().getSubordinates().add(user);
		//invitationRepository.delete(invitation);//todo remove invitation entry after registration?
		return user;
	}

	public String createInvitationLink(UserInvitation userInvitation, long userId) {
		if (userRepository.findByEmail(userInvitation.getEmail()).isPresent()) {
			throw new DuplicateEmailException();
		}

		String invitationToken = RandomStringUtils.randomAlphanumeric(32);
		User userWhoInvited = userRepository
				.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), userId));
		Invitation invitation = new Invitation(
				userInvitation.getEmail(),
				invitationToken,
				userWhoInvited
		);
		invitationRepository.save(invitation);

		return new StringBuilder("http://")
				.append(domain)
				.append(':')
				.append(port)
				.append("signupemail/")
				.append(invitationToken)
				.toString();
	}

	public UserInfo getUserInfo(Long userId) {
		return userRepository.findById(userId)
				.map(user -> new UserInfo(user.getEmail(), user.getName()))
				.orElseThrow(IllegalStateException::new);
	}

}
