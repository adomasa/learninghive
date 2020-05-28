package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class WrongPasswordException extends RuntimeException {

	private static final long serialVersionUID = -543869351208554131L;

	public WrongPasswordException() {
		super("Wrong password");
	}
}
