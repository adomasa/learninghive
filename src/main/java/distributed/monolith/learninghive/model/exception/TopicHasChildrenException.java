package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TopicHasChildrenException extends RuntimeException {

	private static final long serialVersionUID = -6863445203578777368L;

	public TopicHasChildrenException() {
		super("Topic has children");
	}
}
