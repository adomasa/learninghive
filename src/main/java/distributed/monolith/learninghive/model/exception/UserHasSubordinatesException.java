package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserHasSubordinatesException extends RuntimeException {
	private static final long serialVersionUID = 1491979999862473991L;

	public UserHasSubordinatesException() {
		super("User has subordinates");
	}
}
