package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static distributed.monolith.learninghive.util.JwtUtil.HEADER_TOKEN;

public class JwtTokenFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain chain)
			throws ServletException, IOException {
		Optional<String> token = JwtUtil.resolveToken(request.getHeader(HEADER_TOKEN));
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
