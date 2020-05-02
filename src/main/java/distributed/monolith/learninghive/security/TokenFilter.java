package distributed.monolith.learninghive.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public abstract class TokenFilter extends OncePerRequestFilter {

	protected AuthTokenProvider authTokenProvider;

	public TokenFilter(AuthTokenProvider authTokenProvider) {
		this.authTokenProvider = authTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain chain)
			throws ServletException, IOException {

		Optional<String> token = resolveToken(request);
		if (token.isPresent()) {
			Optional<Authentication> authentication =
					authTokenProvider.getAuthentication(token.get());

			if (authentication.isPresent()) {
				SecurityContextHolder.getContext().setAuthentication(authentication.get());
			} else {
				SecurityContextHolder.clearContext();
			}
		}

		chain.doFilter(request, response);
	}

	protected abstract Optional<String> resolveToken(HttpServletRequest request);
}
