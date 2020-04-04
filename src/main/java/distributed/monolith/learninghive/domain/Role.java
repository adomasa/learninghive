package distributed.monolith.learninghive.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
	/**
	 * Super-user, single instance only
	 */
	ADMIN,
	/**
	 * Team leaders, team members
	 */
	CLIENT;

	@Override
	public String getAuthority() {
		return name();
	}

}