package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Invitation;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.domain.UserRefreshToken;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.exception.WrongPasswordException;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserLogin;
import distributed.monolith.learninghive.model.response.TokenPair;
import distributed.monolith.learninghive.repository.LinkRepository;
import distributed.monolith.learninghive.repository.UserRefreshTokenRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

	private final UserRefreshTokenRepository userRefreshTokenRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final LinkRepository linkRepository;

	private final JwtTokenProvider tokenProvider;

	@Value("${server:port}")
	private String port;
	@Value("${server:domain}")
	private String domain;

	public TokenPair doLoginUser(User user) {
		String jwt = tokenProvider.createToken(user.getId(), user.getRoles());
		String refreshToken = createRefreshToken(user);
		return new TokenPair(jwt, refreshToken);
	}

	/**
	 * @return newly generated access token or nothing, if the refresh token is not valid
	 */
	public Optional<TokenPair> refreshAccessTokens(String refreshToken) {
		LOG.debug("AccountService::refreshAccessTokens, refreshToken={}", refreshToken);
		return userRefreshTokenRepository.findByToken(refreshToken)
				.map(userRefreshToken -> doLoginUser(userRefreshToken.getUser()));
	}

	/**
	 * Currently, it doesn't expire. Ideally it should have expiration date
	 */
	private String createRefreshToken(User user) {
		Optional<UserRefreshToken> oldToken = userRefreshTokenRepository.findByUser(user);
		String newTokenValue = RandomStringUtils.randomAlphanumeric(128);

		UserRefreshToken newRefreshToken;
		if (oldToken.isPresent()) {
			UserRefreshToken refreshToken = oldToken.get();
			refreshToken.setToken(newTokenValue);
			newRefreshToken = refreshToken;
		} else {
			newRefreshToken = new UserRefreshToken(newTokenValue, user);
		}

		userRefreshTokenRepository.save(newRefreshToken);

		return newTokenValue;
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
				.orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName()));
	}

	/**
	 * Remove refresh token from database
	 */
	public void logoutUser(long userId) {
		userRefreshTokenRepository.findByUserId(userId)
				.ifPresent(userRefreshTokenRepository::delete);
	}

	public String createInvitationLink(UserInvitation userInvitation, long userId) {
		Optional<User> userWhoInvited = userRepository.findById(userId);

		StringBuilder str = new StringBuilder("http://");
		str.append(domain);
		str.append(":");
		str.append(port);
		str.append("signupemail/");
		String randomString = RandomStringUtils.randomAlphanumeric(32);
		str.append(randomString);

		if (userWhoInvited.isPresent()) {
			Invitation invitation = new Invitation(
					userInvitation.getEmail(),
					randomString,
					userWhoInvited.get()
			);
			linkRepository.save(invitation);
		}

		return str.toString();
	}
}
