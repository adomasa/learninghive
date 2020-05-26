package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.domain.UserRefreshToken;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.exception.WrongPasswordException;
import distributed.monolith.learninghive.model.request.UserLogin;
import distributed.monolith.learninghive.model.request.UserRequest;
import distributed.monolith.learninghive.model.response.TokenPair;
import distributed.monolith.learninghive.repository.UserRefreshTokenRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.security.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final UserRefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private final AuthTokenProvider authTokenProvider;

	@Override
	public TokenPair doLoginUser(User user) {
		String jwt = authTokenProvider.createToken(user.getId(), user.getRole());
		String refreshToken = createRefreshToken(user);
		return new TokenPair(jwt, refreshToken);
	}

	/**
	 * @return newly generated access token or nothing, if the refresh token is not valid
	 */
	@Override
	public Optional<TokenPair> refreshAccessTokens(String refreshToken) {
		return refreshTokenRepository.findByToken(refreshToken)
				.map(userRefreshToken -> doLoginUser(userRefreshToken.getUser()));
	}

	@Override
	public TokenPair loginUser(UserLogin userLogin) {
		return userRepository.findByEmail(userLogin.getEmail())
				.map(user -> {
					if (passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
						return doLoginUser(user);
					} else {
						throw new WrongPasswordException();
					}
				})
				.orElseThrow(() -> new ResourceNotFoundException(User.class, userLogin.getEmail()));
	}

	/**
	 * Currently, it doesn't expire. Ideally it should have expiration date
	 */
	@Override
	public String createRefreshToken(User user) {
		String newTokenValue = RandomStringUtils.randomAlphanumeric(128);
		UserRefreshToken newRefreshToken = refreshTokenRepository
				.findByUser(user)
				.map(token -> {
					token.setToken(newTokenValue);
					return token;
				})
				.orElse(new UserRefreshToken(newTokenValue, user));

		refreshTokenRepository.save(newRefreshToken);

		return newTokenValue;
	}

	@Override
	public void updateAccountData(Long userId, UserRequest userRequest) {
		var userToUpdate = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class,
						userId
				));

		userToUpdate.setEmail(userRequest.getEmail());
		userToUpdate.setName(userRequest.getName());
		userToUpdate.setSurname(userRequest.getSurname());
		userToUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		userToUpdate.setVersion(userRequest.getVersion());

		userRepository.save(userToUpdate); //todo may throw Optimistic Locking exception
	}

	/**
	 * Remove refresh token from database
	 */
	@Override
	public void logoutUser(long userId) {
		refreshTokenRepository.findByUserId(userId)
				.ifPresent(refreshTokenRepository::delete);
	}
}