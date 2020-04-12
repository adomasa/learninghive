package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
	private static final String ERROR_FORMAT = "Invalid %s token: %s";


	private static final long serialVersionUID = -1458815167089353006L;

	public InvalidTokenException(String type, String token) {
		super(String.format(ERROR_FORMAT, type, token));
	}

}
