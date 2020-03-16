package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateEmailException;
import distributed.monolith.learninghive.model.exception.UserNotFoundException;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.response.UserInfo;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void delete(String email) {
		userRepository.deleteByEmail(email)
				.orElseThrow(UserNotFoundException::new);
	}

	public User search(UserRegistration userRegistration) {
		return userRepository.findByEmail(userRegistration.getEmail())
				.orElseThrow(UserNotFoundException::new);
	}

	public User registerUser(UserRegistration userRegistration, List<Role> roles) {
		if (userRepository.findByEmail(userRegistration.getEmail()).isPresent()) {
			throw new DuplicateEmailException();
		}

		User user = new User(
				userRegistration.getEmail(),
				passwordEncoder.encode(userRegistration.getPassword()),
				userRegistration.getName(),
				userRegistration.getSurname(),
				roles
		);

		userRepository.save(user);
		return user;
	}

	public UserInfo getUserInfo(Long userId) {
		return userRepository.findById(userId)
				.map(user -> new UserInfo(user.getEmail(), user.getName()))
				.orElseThrow(IllegalStateException::new);
	}

}
