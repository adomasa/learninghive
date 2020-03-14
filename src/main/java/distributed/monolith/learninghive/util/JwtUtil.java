package distributed.monolith.learninghive.util;

import java.util.Optional;

public final class JwtUtil {
	public static final String HEADER_TOKEN = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";

	private JwtUtil() {
	}

	public static Optional<String> resolveToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
			return Optional.of(bearerToken.substring(7));
		}
		return Optional.empty();
	}
}
