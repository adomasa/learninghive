package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Invitation;
import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.InvalidTokenException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.exception.UserHasSubordinatesException;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.request.UserRequest;
import distributed.monolith.learninghive.model.response.UserInfo;
import distributed.monolith.learninghive.repository.InvitationRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	public static final int INVITATION_TOKEN_SIZE = 32;
	public static final String TOKEN_ARG_NAME = "token";

	private final InvitationRepository invitationRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;

	@Value("${frontend.url}")
	private String frontendUrl;

	@Value("${frontend.scheme}")
	private String frontendScheme;

	@Value("${frontend.registrationPath}")
	private String frontendRegistrationPath;

	@Override
	public void delete(String email) {
		var user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email
				));

		if (!user.getSubordinates().isEmpty()) {
			throw new UserHasSubordinatesException();
		}

		userRepository.deleteByEmail(email);
	}

	@Override
	@Transactional
	public User registerUser(String invitationToken, UserRegistration userRegistration, List<Role> roles) {
		var invitation = invitationRepository.findByValidationToken(invitationToken)
				.orElseThrow(() -> new InvalidTokenException("invitation", invitationToken));
		User user = doRegister(invitation, userRegistration, roles);
		invitation.getUserWhoInvited().getSubordinates().add(user);
		invitationRepository.delete(invitation);
		return user;
	}

	private User doRegister(Invitation invitation, UserRegistration userRegistration, List<Role> roles) {
		var user = User.builder()
				.email(invitation.getEmail())
				.password(passwordEncoder.encode(userRegistration.getPassword()))
				.name(userRegistration.getName())
				.surname(userRegistration.getSurname())
				.roles(roles)
				.supervisor(invitation.getUserWhoInvited())
				.build();

		return userRepository.save(user);
	}


	@Override
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
				.scheme(frontendScheme)
				.host(frontendUrl)
				.path(frontendRegistrationPath)
				.queryParam(TOKEN_ARG_NAME, invitation.getValidationToken()).build()
				.toUriString();
	}

	@Override
	public UserInfo getUserInfo(Long userId) {
		return userRepository.findById(userId)
				.map(user -> modelMapper.map(user, UserInfo.class))
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						userId
				));
	}

	@Override
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


	@Override
	public List<User> getUserSubordinates(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email
				))
				.getSubordinates();
	}

	@Override
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
	}
}
