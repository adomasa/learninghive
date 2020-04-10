package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateEmailException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.exception.TopicHasObjectivesException;
import distributed.monolith.learninghive.model.exception.UserHasSubordinatesException;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.request.UserRequest;
import distributed.monolith.learninghive.model.response.UserInfo;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void delete(String email) {

		if (userRepository.findByEmail(email).isPresent()) {
			if(userRepository.findByEmail(email).get().getSubordinates().isEmpty()){
				userRepository.deleteByEmail(email);
			}
			else throw new UserHasSubordinatesException();
		}
		else throw new ResourceNotFoundException(
				User.class.getSimpleName(),
				email);

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

	public void updateUser(String email, UserRequest userRequest){
		if(userRepository.findByEmail(email).isPresent()){
			var userToUpdate = userRepository.findByEmail(email).get();
			userToUpdate.setEmail(userRequest.getEmail());
			userToUpdate.setName(userRequest.getName());
			userToUpdate.setSurname(userRequest.getSurname());
			userToUpdate.setPassword(userRequest.getPassword());
		}
		else throw new ResourceNotFoundException(
				User.class.getSimpleName(),
				email
		);
	}

	public void updateUserEmail(String email) {
		if(userRepository.findByEmail(email).isPresent()){
			userRepository.findByEmail(email).get().setEmail(email);
		}
		else throw new ResourceNotFoundException(
				User.class.getSimpleName(),
				email
		);
	}

	public List<User> getUserSubordinates(String email) {
		if(userRepository.findByEmail(email).isPresent()){
			return userRepository.findByEmail(email).get().getSubordinates();
		}
		else throw new ResourceNotFoundException(
				User.class.getSimpleName(),
				email
		);
	}

	public void addUserSubordinate(String emailSupervisor, String emailSubordinate) {
		if(userRepository.findByEmail(emailSupervisor).isPresent()
				&& userRepository.findByEmail(emailSubordinate).isPresent()){
			userRepository.findByEmail(emailSupervisor)
					.get()
					.getSubordinates()
					.add(userRepository.findByEmail(emailSubordinate).get());
		}
		else throw new ResourceNotFoundException(
				User.class.getSimpleName(),
				emailSupervisor + " " + emailSubordinate
		);
	}

	public void deleteUserSubordinate(String emailSupervisor, String emailSubordinate) {
		if(userRepository.findByEmail(emailSupervisor).isPresent()
				&& userRepository.findByEmail(emailSubordinate).isPresent()){
			userRepository.findByEmail(emailSupervisor)
					.get()
					.getSubordinates()
					.remove(userRepository.findByEmail(emailSubordinate).get());
		}
		else throw new ResourceNotFoundException(
				User.class.getSimpleName(),
				emailSupervisor + " " + emailSubordinate
		);
	}
}
