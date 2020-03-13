package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.InvalidRefreshTokenException;
import distributed.monolith.learninghive.model.request.RefreshToken;
import distributed.monolith.learninghive.model.request.UserLogin;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.response.TokenPair;
import distributed.monolith.learninghive.model.response.UserInfo;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.AccountService;
import distributed.monolith.learninghive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
public class AccountController {

	private final AccountService accountService;
	private final UserService userService;
	private final SecurityService securityService;

	@Autowired
	public AccountController(AccountService accountService,
	                         UserService userService,
	                         SecurityService securityService) {
		this.accountService = accountService;
		this.userService = userService;
		this.securityService = securityService;
	}

	@PostMapping(path = ACCOUNT_REGISTER)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody
	TokenPair registerUser(@Valid @RequestBody UserRegistration userRegistration) {
		User user = userService.registerUser(userRegistration, Collections.singletonList(Role.CLIENT));
		return accountService.doLoginUser(user);
	}

	@PostMapping(path = ACCOUNT_REFRESH)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	TokenPair tokenRefresh(@Valid @RequestBody RefreshToken refreshToken) {
		return accountService.refreshAccessTokens(refreshToken.getRefreshToken())
				.orElseThrow(InvalidRefreshTokenException::new);
	}

	@PostMapping(path = ACCOUNT_LOGIN)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	TokenPair loginUser(@Valid @RequestBody UserLogin userLogin) {
		return accountService.loginUser(userLogin);
	}

	@DeleteMapping(path = ACCOUNT_LOGOUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logoutUser(@Valid @RequestBody RefreshToken refreshToken) {
		accountService.logoutUser(refreshToken.getRefreshToken());
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = ACCOUNT_INFO)
	public @ResponseBody
	UserInfo findUserInfo() {
		Long userId = securityService.getLoggedUserId();
		return userService.getUserInfo(userId);
	}

}
