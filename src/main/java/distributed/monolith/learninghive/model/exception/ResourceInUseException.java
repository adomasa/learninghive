package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceInUseException extends RuntimeException {

	private static final long serialVersionUID = -618529787486992309L;

	public ResourceInUseException(String resource, Long id, String ownerResource) {
		super(String.format("%s with id %d is used by %s", resource, id, ownerResource));
	}
}
