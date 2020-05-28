package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.security.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static distributed.monolith.learninghive.security.util.JwtUtil.HEADER_TOKEN;

public class JwtTokenFilter extends TokenFilter {

	public JwtTokenFilter(AuthTokenProvider authTokenProvider) {
		super(authTokenProvider);
	}

	@Override
	protected Optional<String> resolveToken(HttpServletRequest request) {
		return JwtUtil.resolveToken(request.getHeader(HEADER_TOKEN));
	}
}
