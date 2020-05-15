package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.InsufficientAuthorityException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorityServiceImpl implements AuthorityService {
	private final SecurityService securityService;
	private final UserRepository userRepository;

	@Override
	public boolean isNotLoggedUser(@Nullable Long userId) {
		return userId != null && securityService.getLoggedUserId() != userId;
	}

	@Override
	public boolean isLoggedUserSupervisorOf(@Nullable Long userId) {
		if (userId == null) {
			return false;
		}

		var sourceUserId = securityService.getLoggedUserId();
		return findAllSupervisors(userId).stream()
				.map(User::getId)
				.anyMatch(supervisorId -> sourceUserId == supervisorId);
	}

	@Override
	public void validateLoggedUserOrAdmin(@Nullable Long userId) throws InsufficientAuthorityException {
		if (isNotLoggedUser(userId) && !isLoggedUserMatching(Role.ADMIN)) {
			throw new InsufficientAuthorityException();
		}
	}

	@Override
	public boolean isLoggedUserMatching(Role role) {
		return securityService.getLoggedUserRole() == role;
	}

	@Override
	public List<User> findAllSupervisors(@Nullable Long userId) {
		if (userId == null) {
			return Collections.emptyList();
		}

		var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class,
						userId));

		var allSupervisors = new ArrayList<User>();
		var currentSupervisor = user.getSupervisor();
		while (currentSupervisor != null) {
			allSupervisors.add(currentSupervisor);
			currentSupervisor = currentSupervisor.getSupervisor();
		}
		return allSupervisors;
	}

	@Override
	public void validateLoggedUserOrSupervisor(@NonNull User owner) throws InsufficientAuthorityException {
		var ownerId = owner.getId();
		if (isNotLoggedUser(ownerId) && isLoggedUserSupervisorOf(ownerId)) {
			throw new InsufficientAuthorityException();
		}
	}

	@Override
	public void validateLoggedUserOrSupervisor(@Nullable Long targetUserId) throws InsufficientAuthorityException {
		if (isNotLoggedUser(targetUserId) && !isLoggedUserSupervisorOf(targetUserId)) {
			throw new InsufficientAuthorityException();
		}
	}
}
