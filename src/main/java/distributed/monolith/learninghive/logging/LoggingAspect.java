package distributed.monolith.learninghive.logging;

import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Aspect
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class LoggingAspect {
	private final Logger logger;
	private final SecurityService securityService;
	private final UserService userService;

	@Before("execution(* distributed.monolith.learninghive.controller.*.*(..))")
	public void log(JoinPoint joinPoint) {
		Long userId = securityService.getLoggedUserId();
		var calledMethod = String.format("%s.%s", joinPoint.getTarget().getClass().getSimpleName(),
				joinPoint.getSignature().getName());

		if (userId == null) {
			logger.log(String.format("Not logged in user called method %s", calledMethod));
		} else {
			var userInfo = userService.getUserInfo(userId);
			var username = String.format("%s %s", userInfo.getName(), userInfo.getSurname());
			var role = securityService.getLoggedUserRole().toString();

			logger.log(String.format("User %s (%d) with role %s called method %s",
					username, userId, role, calledMethod));
		}
	}
}
