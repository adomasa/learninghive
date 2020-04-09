package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TopicHasObjectivesException extends RuntimeException {

	public TopicHasObjectivesException() {
		super("Topic has objectives");
	}
}
