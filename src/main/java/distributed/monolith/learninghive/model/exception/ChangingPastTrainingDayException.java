package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ChangingPastTrainingDayException extends RuntimeException {

	private static final long serialVersionUID = -6863445203578777368L;

	public ChangingPastTrainingDayException() {
		super("Cannot make changes to training day that is in the past");
	}
}
