package distributed.monolith.learninghive.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * Hacky implementation of Spring Authorities
 */
public enum Role implements GrantedAuthority {
	/**
	 * Super-user, single instance only
	 */
	ADMIN,
	/**
	 * Supervisors
	 */
	SUPERVISOR,
	/**
	 * Employees
	 */
	EMPLOYEE;

	@Override
	public String getAuthority() {
		return name();
	}

}