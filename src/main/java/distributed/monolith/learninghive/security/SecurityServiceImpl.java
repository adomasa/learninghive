package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.model.security.JwtAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

	@Override
	public long getLoggedUserId() {
		var authentication =
				(JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
		return authentication.getUserId();
	}

}