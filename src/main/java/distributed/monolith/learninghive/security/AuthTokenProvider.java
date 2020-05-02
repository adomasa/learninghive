package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.domain.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AuthTokenProvider {
	String createToken(Long userId, List<Role> roles);

	Long getUserId(String token);

	Collection<SimpleGrantedAuthority> getAuthorities(String token);

	Optional<Authentication> getAuthentication(String token);
}
