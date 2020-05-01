package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${admin.username:admin}")
	private String defaultUsername;

	@Value("${admin.password:admin}")
	private String defaultPassword;

	@Override
	@PostConstruct
	public void initialiseAdminUser() {
		if (userRepository.findByRoles(Role.ADMIN).isEmpty()) {
			if (!userRepository.findByRoles(Role.CLIENT).isEmpty()) {
				throw new IllegalStateException("Admin can be created only on empty DB");
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("Initialising admin user");
			}

			var admin = User.builder()
					.email(defaultUsername)
					.password(passwordEncoder.encode(defaultPassword))
					.name("admin")
					.surname("admin")
					.roles(Arrays.asList(Role.ADMIN, Role.CLIENT))
					.build();

			userRepository.save(admin);
		}
	}

}
