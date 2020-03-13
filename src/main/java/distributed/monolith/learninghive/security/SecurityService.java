package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.model.security.JwtAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

	public long getLoggedUserId() {
		JwtAuthentication authentication =
				(JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
		return authentication.getUserId();
	}

}
