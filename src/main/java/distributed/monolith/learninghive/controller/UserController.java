package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.request.UserRequest;
import distributed.monolith.learninghive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@DeleteMapping(path = USER_DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@RequestParam(name = "email") String email) {
		userService.delete(email);
	}

	@PutMapping(path = USER_UPDATE)
	@ResponseStatus(HttpStatus.OK)
	public void updateUser(@RequestParam(name = "email") String email, @Valid @RequestBody UserRequest userRequest) {
		userService.updateUser(email, userRequest);
	}

	@GetMapping(path = USER_QUERY)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	List<User> queryUserSubordinates(@RequestParam(name = "email") String email) {
		return userService.getUserSubordinates(email);
	}

	@PostMapping(path = USER_SUPERVISOR)
	@ResponseStatus(HttpStatus.OK)
	public void updateUserSupervisor(@RequestParam(name = "emailSupervisor") String emailSupervisor,
	                                 @RequestParam(name = "emailSubordinate") String emailSubordinate) {
		userService.updateUserSupervisor(emailSupervisor, emailSubordinate);
	}
}