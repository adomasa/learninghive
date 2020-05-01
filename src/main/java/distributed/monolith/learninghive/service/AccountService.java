package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.request.UserLogin;
import distributed.monolith.learninghive.model.response.TokenPair;

import java.util.Optional;

public interface AccountService {
	TokenPair doLoginUser(User user);

	Optional<TokenPair> refreshAccessTokens(String refreshToken);

	String createRefreshToken(User user);

	TokenPair loginUser(UserLogin userLogin);

	void logoutUser(long userId);
}
