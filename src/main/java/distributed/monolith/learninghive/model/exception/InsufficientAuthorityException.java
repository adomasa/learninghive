package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InsufficientAuthorityException extends RuntimeException {

	private static final long serialVersionUID = -1369226410670594313L;

	public InsufficientAuthorityException() {
		super("Insufficient authority");
	}
}
