package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceDoesNotBelongToUser extends RuntimeException {

	private static final long serialVersionUID = -7765585126624241007L;

	public ResourceDoesNotBelongToUser(Class<?> resource, Long id, Long userId) {
		super(String.format("%s with id %d does not belong to user with id %s", resource.getSimpleName(), id, userId));
	}
}
