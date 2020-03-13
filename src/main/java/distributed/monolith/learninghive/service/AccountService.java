package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.domain.UserRefreshToken;
import distributed.monolith.learninghive.model.exception.UserNotFoundException;
import distributed.monolith.learninghive.model.exception.WrongPasswordException;
import distributed.monolith.learninghive.model.request.UserLogin;
import distributed.monolith.learninghive.model.response.TokenPair;
import distributed.monolith.learninghive.repository.UserRefreshTokenRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.security.JwtTokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

	private final UserRefreshTokenRepository userRefreshTokenRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private final JwtTokenProvider tokenProvider;

	@Autowired
	public AccountService(UserRefreshTokenRepository userRefreshTokenRepository,
	                      UserRepository userRepository,
	                      PasswordEncoder passwordEncoder,
	                      JwtTokenProvider tokenProvider) {
		this.userRefreshTokenRepository = userRefreshTokenRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
	}

	public TokenPair doLoginUser(User user) {
		String jwt = tokenProvider.createToken(user.getId(), user.getRoles());
		String refreshToken = createRefreshToken(user);
		return new TokenPair(jwt, refreshToken);
	}

	/**
	 * @return newly generated access token or nothing, if the refresh token is not valid
	 */
	public Optional<TokenPair> refreshAccessTokens(String refreshToken) {
		return userRefreshTokenRepository.findByToken(refreshToken)
				.map(userRefreshToken -> doLoginUser(userRefreshToken.getUser()));
	}

	/**
	 * Currently, it doesn't expire. Ideally it should have expiration date
	 */
	private String createRefreshToken(User user) {
		if (userRefreshTokenRepository.removeByUser(user).isPresent()) {
			LOG.debug("Removing previous refresh token");
		}

		String token = RandomStringUtils.randomAlphanumeric(128);
		userRefreshTokenRepository.save(new UserRefreshToken(token, user));
		return token;
	}


	public TokenPair loginUser(UserLogin userLogin) {
		return userRepository.findByEmail(userLogin.getEmail())
				.map(user -> {
					if (passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
						return doLoginUser(user);
					} else {
						throw new WrongPasswordException();
					}
				})
				.orElseThrow(UserNotFoundException::new);
	}


	public void logoutUser(String refreshToken) {
		userRefreshTokenRepository.findByToken(refreshToken)
				.ifPresent(userRefreshTokenRepository::delete);
	}
}
