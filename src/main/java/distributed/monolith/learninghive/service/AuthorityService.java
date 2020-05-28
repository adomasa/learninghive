package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.authority.AuthorityType;
import distributed.monolith.learninghive.model.exception.InsufficientAuthorityException;
import org.springframework.lang.Nullable;

import java.util.List;

public interface AuthorityService {
	boolean isNotLoggedUser(@Nullable Long userId);

	boolean isLoggedUserSupervisorOf(@Nullable Long userId);

	boolean isLoggedUserSubordinateOf(@Nullable Long userId);

	boolean isLoggedUserRole(Role role);

	List<User> findAllSupervisors(@Nullable Long userId);

	void validateLoggedUserSupervisorOf(@Nullable Long userId) throws InsufficientAuthorityException;

	void validateOriginOneOf(@Nullable Long userId, AuthorityType... types) throws InsufficientAuthorityException;

	/**
	 * @param userId target user id, null value means self.
	 * @throws InsufficientAuthorityException If origin user is not self or does not have admin privileges
	 */
	void validateLoggedUserOrAdmin(@Nullable Long userId) throws InsufficientAuthorityException;

	void validateNotSelf(@Nullable Long userId) throws InsufficientAuthorityException;

	/**
	 * @param targetUserId user whose information is being manipulated
	 * @throws InsufficientAuthorityException if source of request is not self or higher in hierarchy (supervisor)
	 *                                        than targetUserId
	 */
	void validateLoggedUserOrSupervisorOf(@Nullable Long targetUserId) throws InsufficientAuthorityException;

	void validateLoggedUserIsAdmin() throws InsufficientAuthorityException;

	void validateLoggedUserIsSupervisorOf(@Nullable Long targetUserId) throws InsufficientAuthorityException;
}
