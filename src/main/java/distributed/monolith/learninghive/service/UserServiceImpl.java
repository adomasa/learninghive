package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Invitation;
import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.*;
import distributed.monolith.learninghive.model.exception.InvalidTokenException.Type;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.response.UserInfo;
import distributed.monolith.learninghive.repository.InvitationRepository;
import distributed.monolith.learninghive.repository.ObjectiveRepository;
import distributed.monolith.learninghive.repository.TrainingDayRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	public static final int INVITATION_TOKEN_SIZE = 32;
	public static final String TOKEN_ARG_NAME = "token";

	private final InvitationRepository invitationRepository;
	private final UserRepository userRepository;
	private final ObjectiveRepository objectiveRepository;
	private final TrainingDayRepository trainingDayRepository;

	private final SecurityService securityService;

	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;

	@Value("${frontend.url}")
	private String frontendUrl;

	@Value("${frontend.scheme}")
	private String frontendScheme;

	@Value("${frontend.registrationPath}")
	private String frontendRegistrationPath;

	@Override
	@Transactional
	public void delete(long userId) {
		var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class,
						userId
				));

		if (!user.getSubordinates().isEmpty()) {
			throw new UserHasSubordinatesException();
		}

		trainingDayRepository.deleteByUserId(userId);
		objectiveRepository.deleteByUserId(userId);
		userRepository.deleteById(userId);
	}

	@Override
	@Transactional
	public User registerUser(String invitationToken, UserRegistration userRegistration, Role role) {
		var invitation = invitationRepository.findByValidationToken(invitationToken)
				.orElseThrow(() -> new InvalidTokenException(Type.INVITATION, invitationToken));
		var user = doRegister(invitation, userRegistration, role);
		var userWhoInvited = invitation.getUserWhoInvited();
		userWhoInvited.getSubordinates().add(user);
		updateUserRole(userWhoInvited);
		invitationRepository.delete(invitation);
		return user;
	}

	private User doRegister(Invitation invitation, UserRegistration userRegistration, Role role) {
		var user = User.builder()
				.email(invitation.getEmail())
				.password(passwordEncoder.encode(userRegistration.getPassword()))
				.name(userRegistration.getName())
				.surname(userRegistration.getSurname())
				.role(role)
				.supervisor(invitation.getUserWhoInvited())
				.build();

		return userRepository.save(user);
	}


	@Override
	public String createInvitationLink(UserInvitation userInvitation, long userId) {
		if (userRepository.findByEmail(userInvitation.getEmail()).isPresent()) {
			throw new DuplicateResourceException(User.class, "email", userInvitation.getEmail());
		}

		if (invitationRepository.findByEmail(userInvitation.getEmail()).isPresent()) {
			throw new DuplicateResourceException(Invitation.class, "email", userInvitation.getEmail());
		}

		var userWhoInvited = userRepository
				.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

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
	public UserInfo getUserInfo(long userId) {
		return userRepository.findById(userId)
				.map(user -> modelMapper.map(user, UserInfo.class))
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class,
						userId
				));
	}

	@Override
	public List<UserInfo> getUserSubordinates(long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, userId))
				.getSubordinates()
				.stream()
				.map(user -> modelMapper.map(user, UserInfo.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<UserInfo> findTeamMembers(long userId) {
		if (securityService.getLoggedUserRole() == Role.ADMIN) {
			return Collections.emptyList();
		}

		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class,
						userId
				))
				.getSupervisor()
				.getSubordinates()
				.stream()
				.filter(user -> user.getId() != userId)
				.map(user -> modelMapper.map(user, UserInfo.class))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateUserSupervisor(long supervisorId, long subordinateId) {
		var userSupervisor = userRepository.findById(supervisorId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, supervisorId));

		var userSubordinate = userRepository.findById(subordinateId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, subordinateId));

		userSubordinate.getSupervisor().getSubordinates().remove(userSubordinate);
		updateUserRole(userSubordinate.getSupervisor());

		userSubordinate.setSupervisor(userSupervisor);
		if (!userSupervisor.getSubordinates().contains(userSubordinate)) {
			userSupervisor.getSubordinates().add(userSubordinate);
			updateUserRole(userSupervisor);
		}

		userRepository.saveAndFlush(userSubordinate);
		userRepository.saveAndFlush(userSupervisor);

		if (userRepository.isCircularHierarchy(userSupervisor.getId())) {
			throw new CircularHierarchyException(User.class, userSupervisor.getId());
		}
	}

	private void updateUserRole(User user) {
		if (user.getRole() == Role.ADMIN) {
			return;
		}

		// Lost all subordinates
		if (user.getSubordinates().isEmpty() && user.getRole() == Role.SUPERVISOR) {
			user.setRole(Role.EMPLOYEE);
			return;
		}

		// Got first subordinates
		if (!user.getSubordinates().isEmpty() && user.getRole() == Role.EMPLOYEE) {
			user.setRole(Role.SUPERVISOR);
		}
	}
}
