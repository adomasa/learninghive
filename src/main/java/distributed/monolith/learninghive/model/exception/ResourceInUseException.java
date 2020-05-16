package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceInUseException extends RuntimeException {

	private static final long serialVersionUID = -618529787486992309L;

	public ResourceInUseException(Class<?> resource, Long id, Class<?> ownerResource) {
		super(String.format("%s with id %d is used by %s",
				resource.getSimpleName(),
				id,
				ownerResource.getSimpleName()));
	}
}
