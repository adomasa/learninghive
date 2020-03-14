package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AdminService {
	private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);

	private final UserService userService;
	private final UserRepository userRepository;

	@Value("${admin.username:admin}")
	private String defaultUsername;

	@Value("${admin.password:admin}")
	private String defaultPassword;

	@PostConstruct
	public void initialiseAdminUser() {
		if (userRepository.findByRoles(Role.ADMIN).isEmpty()) {
			LOG.info("Initialising admin user");
			UserRegistration admin = new UserRegistration(
					defaultUsername,
					defaultPassword,
					"admin",
					"admin"
			);

			userService.registerUser(admin, Arrays.asList(Role.ADMIN, Role.CLIENT));
		}
	}

}
