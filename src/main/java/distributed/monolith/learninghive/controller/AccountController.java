package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.model.exception.InvalidTokenException;
import distributed.monolith.learninghive.model.exception.InvalidTokenException.Type;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserLogin;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.request.UserRequest;
import distributed.monolith.learninghive.model.response.TokenPair;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.AccountService;
import distributed.monolith.learninghive.service.InvitationProviderService;
import distributed.monolith.learninghive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final UserService userService;
	private final SecurityService securityService;

	private final InvitationProviderService invitationProviderService;

	@PostMapping(path = ACCOUNT_REGISTER)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody
	TokenPair registerUser(@RequestParam("token") String invitationToken,
	                       @Valid @RequestBody UserRegistration userRegistration) {
		var user = userService.registerUser(invitationToken, userRegistration, Role.EMPLOYEE);
		return accountService.doLoginUser(user);
	}

	@PostMapping(path = ACCOUNT_REFRESH)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	TokenPair tokenRefresh(@RequestParam("token") String refreshToken) {
		return accountService.refreshAccessTokens(refreshToken)
				.orElseThrow(() -> new InvalidTokenException(Type.REFRESH, refreshToken));
	}

	@PostMapping(path = ACCOUNT_LOGIN)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	TokenPair loginUser(@Valid @RequestBody UserLogin userLogin) {
		return accountService.loginUser(userLogin);
	}

	@PutMapping(path = ACCOUNT)
	@ResponseStatus(HttpStatus.OK)
	public void updateAccount(@RequestParam(name = "id", required = false) Long userId,
	                          @Valid @RequestBody UserRequest userRequest) {
		accountService.updateAccountData(userId == null ? securityService.getLoggedUserId() : userId, userRequest);
	}

	@DeleteMapping(path = ACCOUNT_LOGOUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logoutUser() {
		var userId = securityService.getLoggedUserId();
		accountService.logoutUser(userId);
	}

	@PostMapping(path = ACCOUNT_INVITE)
	public @ResponseBody
	void sendGeneratedRegistrationLink(@Valid @RequestBody UserInvitation userInvitation) {
		var userId = securityService.getLoggedUserId();
		CompletableFuture.runAsync(() -> {
			var invitationData = userService.createInvitation(userInvitation, userId);
			invitationProviderService.send(invitationData);
		});
	}
}
