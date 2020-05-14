package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.domain.Role;

public interface SecurityService {
	long getLoggedUserId();

	/**
	 * Only one role per user
	 */
	Role getLoggedUserRole();
}

