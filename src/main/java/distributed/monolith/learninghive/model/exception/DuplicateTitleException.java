package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateTitleException extends RuntimeException {

	private static final long serialVersionUID = 1802410866147042000L;

	public DuplicateTitleException() {
		super("Such topic title already exists");
	}
}
