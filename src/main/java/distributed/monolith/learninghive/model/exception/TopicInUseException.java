package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TopicInUseException extends RuntimeException {

	public TopicInUseException() {
		super("Topic has children");
	}
}
