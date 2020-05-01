package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.model.security.JwtAuthentication;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider implements AuthTokenProvider {
	private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);
	private static final String CLAIM_ROLE = "role";

	@Value("${security.jwt.token.secret}")
	private String secret;

	@Value("${security.jwt.token.expireLength:900000}")
	private long validityInMilliseconds;

	@Override
	public String createToken(Long userId, List<Role> roles) {
		Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
		claims.put(CLAIM_ROLE,
				roles.stream()
						.map(s -> new SimpleGrantedAuthority(s.getAuthority()))
						.collect(Collectors.toList()));

		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}

	@Override
	public Long getUserId(String token) {
		String userId = Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();

		return Long.valueOf(userId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<SimpleGrantedAuthority> getAuthorities(String token) {
		return (Collection<SimpleGrantedAuthority>) Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody()
				.get(CLAIM_ROLE);
	}

	@Override
	public Optional<Authentication> getAuthentication(String token) {
		try {
			var userId = getUserId(token);
			var userAuthorities = getAuthorities(token);
			return Optional.of(new JwtAuthentication(userId, userAuthorities));
		} catch (ExpiredJwtException e) {
			LOG.debug("Received expired token: {}", e.getMessage());
		} catch (JwtException | IllegalArgumentException e) {
			LOG.warn("Received invalid token: {} ", e.getMessage());
		}
		return Optional.empty();
	}
}
