package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.model.security.JwtAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {
	@Override
	public Long getLoggedUserId() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof JwtAuthentication) {
			return ((JwtAuthentication) authentication).getUserId();
		}

		return null;
	}

	@Override
	public Role getLoggedUserRole() {
		var authentication =
				(JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
		return (Role) authentication.getAuthorities().iterator().next();
	}

}
