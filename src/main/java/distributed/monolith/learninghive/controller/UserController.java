package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.response.UserInfo;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.AuthorityService;
import distributed.monolith.learninghive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final AuthorityService authorityService;
	private final SecurityService securityService;

	@DeleteMapping(path = USER_DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@RequestParam(name = "id", required = false) long userId) {
		authorityService.validateLoggedUserOrAdmin(userId);
		userService.delete(userId);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = USER_INFO)
	public @ResponseBody
	UserInfo findUserInfo(@RequestParam(name = "id", required = false) Long userId) {
		authorityService.validateLoggedUserOrSupervisor(userId);
		return userService.getUserInfo(userId == null ? securityService.getLoggedUserId() : userId);
	}

	@GetMapping(path = USER_SUBORDINATES)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	List<UserInfo> findUserSubordinates(@RequestParam(name = "id", required = false) Long userId) {
		authorityService.validateLoggedUserOrSupervisor(userId);
		return userService.getUserSubordinates(userId == null ? securityService.getLoggedUserId() : userId);
	}

	@GetMapping(path = USER_TEAM)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	List<UserInfo> findTeamMembers(@RequestParam(name = "userId", required = false) Long userId) {
		authorityService.validateLoggedUserOrSupervisor(userId);
		return userService.findTeamMembers(userId == null ? securityService.getLoggedUserId() : userId);
	}

	@PostMapping(path = USER_SUPERVISOR)
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize("hasAuthority('ADMIN')")
	public void updateUserSupervisor(@RequestParam(name = "supervisorId") Long supervisorId,
	                                 @RequestParam(name = "subordinateId") Long subordinateId) {
		userService.updateUserSupervisor(supervisorId, subordinateId);
	}
}
