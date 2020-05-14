package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CircularHierarchyException extends RuntimeException {
	private static final long serialVersionUID = 4341705841554249218L;

	public CircularHierarchyException(Class<?> resource) {
		super(String.format("Resource %s hierarchy contains circular references", resource.getSimpleName()));
	}

	public CircularHierarchyException(Class<?> resource, long id) {
		super(String.format("Resource %s with id %d hierarchy contains circular references",
				resource.getSimpleName(),
				id));
	}
}
