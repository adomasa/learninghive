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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final UserRefreshTokenRepository refreshTokenRepository;
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
		return refreshTokenRepository.findByToken(refreshToken)
				.map(userRefreshToken -> doLoginUser(userRefreshToken.getUser()));
	}

	/**
	 * Currently, it doesn't expire. Ideally it should have expiration date
	 */
	private String createRefreshToken(User user) {
		Optional<UserRefreshToken> oldToken = refreshTokenRepository.findByUser(user);
		String newTokenValue = RandomStringUtils.randomAlphanumeric(128);

		UserRefreshToken newRefreshToken;
		if (oldToken.isPresent()) {
			UserRefreshToken refreshToken = oldToken.get();
			refreshToken.setToken(newTokenValue);
			newRefreshToken = refreshToken;
		} else {
			newRefreshToken = new UserRefreshToken(newTokenValue, user);
		}

		refreshTokenRepository.save(newRefreshToken);

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
		refreshTokenRepository.findByUserId(userId)
				.ifPresent(refreshTokenRepository::delete);
	}

	public String createInvitationLink(UserInvitation userInvitation, long userId) {
		User userWhoInvited = userRepository
				.findById(userId)
				.orElseThrow(() -> new IllegalStateException("User " + userId + " not found on DB"));

		String invitationToken = RandomStringUtils.randomAlphanumeric(32);
		Invitation invitation = new Invitation(
				userInvitation.getEmail(),
				invitationToken,
				userWhoInvited
		);
		linkRepository.save(invitation);

		return new StringBuilder("http://")
				.append(domain)
				.append(':')
				.append(port)
				.append("signupemail/")
				.append(invitationToken)
				.toString();
	}
}