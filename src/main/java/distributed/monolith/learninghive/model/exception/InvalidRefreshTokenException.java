package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidRefreshTokenException extends RuntimeException {

	private static final long serialVersionUID = -1458815167089353006L;

	public InvalidRefreshTokenException() {
		super("Invalid refresh token");
	}

}
