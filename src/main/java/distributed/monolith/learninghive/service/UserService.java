package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Invitation;
import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.*;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.request.UserRequest;
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

		var user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email
				));

		if (user.getSubordinates().isEmpty()) {
			userRepository.deleteByEmail(email);
		} else {
			throw new UserHasSubordinatesException();
		}

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

	public void updateUser(String email, UserRequest userRequest) {

		var userToUpdate = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email
				));

		userToUpdate.setEmail(userRequest.getEmail());
		userToUpdate.setName(userRequest.getName());
		userToUpdate.setSurname(userRequest.getSurname());
		userToUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));

		userRepository.save(userToUpdate);
	}


	public List<User> getUserSubordinates(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email
				))
				.getSubordinates();
	}

	@Transactional
	public void updateUserSupervisor(String emailSupervisor, String emailSubordinate) {
		var userSupervisor = userRepository.findByEmail(emailSupervisor)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						emailSupervisor
				));

		var userSubordinate = userRepository.findByEmail(emailSubordinate)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						emailSubordinate
				));

		userSubordinate.getSupervisor().getSubordinates().remove(userSubordinate);
		userSubordinate.setSupervisor(userSupervisor);
		if (!userSupervisor.getSubordinates().contains(userSubordinate)) {
			userSupervisor.getSubordinates().add(userSubordinate);
		}
		userRepository.save(userSubordinate);
		userRepository.save(userSupervisor);

		if (userRepository.circularReferencesExist(userSupervisor.getId())) {
			throw new CircularReferenceException(User.class.getSimpleName());
		}
	}
}
