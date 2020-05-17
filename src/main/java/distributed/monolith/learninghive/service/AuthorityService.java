package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.authority.AuthorityType;
import distributed.monolith.learninghive.model.exception.InsufficientAuthorityException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public interface AuthorityService {
	/**
	 * Request for user data comes from different user
	 *
	 * @param userId target user id, null value means self
	 */
	boolean isNotLoggedUser(@Nullable Long userId);

	/**
	 * @param userId target user id, null value means self
	 */
	boolean isLoggedUserSupervisorOf(@Nullable Long userId);

	boolean isLoggedUserSubordinateOf(@Nullable Long userId);


	void validateOriginOneOf(@Nullable Long userId, AuthorityType... types);

	/**
	 * @param userId target user id, null value means self.
	 * @throws InsufficientAuthorityException If origin user is not self or does not have admin privileges
	 */
	void validateLoggedUserOrAdmin(@Nullable Long userId) throws InsufficientAuthorityException;

	void validateNotSelf(@Nullable Long userId);

	boolean isLoggedUserRole(Role role);

	List<User> findAllSupervisors(@Nullable Long userId);

	/**
	 * @param owner user who owns resource
	 * @throws InsufficientAuthorityException if source of request is not self or higher in hierarchy (supervisor)
	 */
	void validateLoggedUserOrSupervisor(@NonNull User owner) throws InsufficientAuthorityException;

	/**
	 * @param targetUserId user whose information is being manipulated
	 * @throws InsufficientAuthorityException if source of request is not self or higher in hierarchy (supervisor)
	 *                                        than targetUserId
	 */
	void validateLoggedUserOrSupervisor(@Nullable Long targetUserId) throws InsufficientAuthorityException;
}
