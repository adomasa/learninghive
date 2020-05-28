package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static distributed.monolith.learninghive.model.constants.Paths.TEST_DATA;

@RestController
@RequiredArgsConstructor
public class TestDataController {
	private final TestService testService;
	private final SecurityService securityService;

	@PostMapping(path = TEST_DATA)
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('ADMIN')")
	public void addTestData() {
		testService.addTestData();
		testService.addTestUsers(securityService.getLoggedUserId());
		testService.addTestTopicsToUsers();
	}
}
