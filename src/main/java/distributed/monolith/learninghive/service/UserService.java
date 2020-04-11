package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateEmailException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.exception.UserHasSubordinatesException;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.request.UserRequest;
import distributed.monolith.learninghive.model.response.UserInfo;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void delete(String email) {

		var user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email
				));

		if (user.getSubordinates().isEmpty()) {
			userRepository.deleteByEmail(email);
		} else {
			throw new UserHasSubordinatesException();
		}

	}

	public User search(UserRegistration userRegistration) {
		return userRepository.findByEmail(userRegistration.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException(
								User.class.getSimpleName(),
								userRegistration.getEmail()
						)
				);
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

		return userRepository.save(user);
	}

	public UserInfo getUserInfo(Long userId) {
		return userRepository.findById(userId)
				.map(user -> new UserInfo(user.getEmail(), user.getName()))
				.orElseThrow(IllegalStateException::new);
	}

	public void updateUser(String email, UserRequest userRequest) {

		var userToUpdate = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email
				));

		userToUpdate.setEmail(userRequest.getEmail());
		userToUpdate.setName(userRequest.getName());
		userToUpdate.setSurname(userRequest.getSurname());
		userToUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));

		userRepository.save(userToUpdate);
	}


	public List<User> getUserSubordinates(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						email
				))
				.getSubordinates();
	}

	public void updateUserSupervisor(String emailSupervisor, String emailSubordinate) {
		var userSupervisor = userRepository.findByEmail(emailSupervisor)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						emailSupervisor
				));

		var userSubordinate = userRepository.findByEmail(emailSubordinate)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						emailSubordinate
				));

		userSubordinate.setSupervisor(userSupervisor);
		userSupervisor.getSubordinates().add(userSubordinate);
		userRepository.save(userSubordinate);
		userRepository.save(userSupervisor);
	}
}
