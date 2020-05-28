package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.authority.AuthorityType;
import distributed.monolith.learninghive.model.exception.InsufficientAuthorityException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.security.SecurityService;
import lombok.RequiredArgsConstructor;
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
		return userId != null && securityService.getLoggedUserId().longValue() != userId.longValue();
	}

	@Override
	public boolean isLoggedUserSupervisorOf(@Nullable Long userId) {
		if (userId == null) {
			return false;
		}

		var sourceUserId = securityService.getLoggedUserId();
		return findAllSupervisors(userId).parallelStream()
				.anyMatch(supervisor -> sourceUserId == supervisor.getId());
	}

	@Override
	public boolean isLoggedUserSubordinateOf(@Nullable Long userId) {
		if (userId == null) {
			return false;
		}

		var sourceUserId = securityService.getLoggedUserId();

		return findAllSupervisors(sourceUserId).parallelStream()
				.anyMatch(supervisor -> supervisor.getId() == userId);
	}

	@Override
	public void validateOriginOneOf(@Nullable Long userId, AuthorityType... types) {
		boolean validated = false;
		for (AuthorityType type : types) {
			switch (type) {
				case SELF:
					validated |= !isNotLoggedUser(userId);
					break;
				case SUBORDINATE:
					validated |= isLoggedUserSubordinateOf(userId);
					break;
				case SUPERVISOR:
					validated |= isLoggedUserSupervisorOf(userId);
					break;
				case ADMIN:
					validated |= isLoggedUserRole(Role.ADMIN);
					break;
				default:
					throw new IllegalStateException("Undefined authorityType: " + type);
			}
		}
		if (!validated) {
			throw new InsufficientAuthorityException();
		}
	}

	@Override
	public void validateLoggedUserOrAdmin(@Nullable Long userId) throws InsufficientAuthorityException {
		if (isNotLoggedUser(userId) && !isLoggedUserRole(Role.ADMIN)) {
			throw new InsufficientAuthorityException();
		}
	}

	@Override
	public void validateNotSelf(Long userId) {
		if (securityService.getLoggedUserId() == userId) {
			throw new IllegalStateException("Origin can't update itself");
		}
	}

	@Override
	public boolean isLoggedUserRole(Role role) {
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
	public void validateLoggedUserSupervisorOf(Long userId) {
		if (!isLoggedUserSupervisorOf(userId)) {
			throw new InsufficientAuthorityException();
		}
	}

	@Override
	public void validateLoggedUserOrSupervisorOf(@Nullable Long targetUserId) throws InsufficientAuthorityException {
		if (isNotLoggedUser(targetUserId) && !isLoggedUserSupervisorOf(targetUserId)) {
			throw new InsufficientAuthorityException();
		}
	}

	@Override
	public void validateLoggedUserIsAdmin() throws InsufficientAuthorityException {
		if (!isLoggedUserRole(Role.ADMIN)) {
			throw new InsufficientAuthorityException();
		}
	}

	@Override
	public void validateLoggedUserIsSupervisorOf(@Nullable Long targetUserId) throws InsufficientAuthorityException {
		if (!isLoggedUserSupervisorOf(targetUserId)) {
			throw new InsufficientAuthorityException();
		}
	}
}
