package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

	private static final String ERROR_FORMAT = "Resource %s with property %s (%s) already exists";

	private static final long serialVersionUID = 3431265431294510711L;

	public DuplicateResourceException(String resource, String property, String propertyValue) {
		super(String.format(ERROR_FORMAT, resource, property, propertyValue));
	}

	public DuplicateResourceException(Exception exception, String resource, String property, String propertyValue) {
		super(String.format(ERROR_FORMAT, resource, property, propertyValue), exception);
	}


}
