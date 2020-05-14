package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.domain.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Optional;

public interface AuthTokenProvider {
	String createToken(Long userId, Role role);

	Long getUserId(String token);

	SimpleGrantedAuthority getAuthority(String token);

	Optional<Authentication> getAuthentication(String token);
}
