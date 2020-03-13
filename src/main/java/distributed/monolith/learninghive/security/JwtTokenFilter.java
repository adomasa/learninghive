package distributed.monolith.learninghive.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtTokenFilter extends OncePerRequestFilter {
	private static final Logger LOG = LoggerFactory.getLogger(JwtTokenFilter.class);
	private static final String HEADER_TOKEN = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;

	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	public Optional<String> resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader(HEADER_TOKEN);
		if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
			return Optional.of(bearerToken.substring(7));
		}
		return Optional.empty();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain chain)
			throws ServletException, IOException {
		Optional<String> token = resolveToken(request);
		if (token.isPresent()) {
			Optional<Authentication> authentication =
					jwtTokenProvider.getAuthentication(token.get());

			if (authentication.isPresent()) {
				SecurityContextHolder.getContext().setAuthentication(authentication.get());
			} else {
				SecurityContextHolder.clearContext();
			}
		}

		chain.doFilter(request, response);
	}
}
