package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserHasSubordinatesException extends RuntimeException {
	public UserHasSubordinatesException() {
		super("User has subordinates");
	}
}
