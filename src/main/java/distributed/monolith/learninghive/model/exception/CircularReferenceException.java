package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CircularReferenceException extends RuntimeException {
	private static final long serialVersionUID = 4341705841554249218L;

	public CircularReferenceException(String resource) {
		super(String.format("Resource %s hierarchy contains circular references", resource));
	}
}
