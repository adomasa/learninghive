package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4265132229531670617L;

	public ResourceNotFoundException(Exception e, String resource) {
		super(String.format("%s not found", resource), e);
	}

	public ResourceNotFoundException(String resource, Long id) {
		super(String.format("%s with id %d not found", resource, id));
	}

	public ResourceNotFoundException(String resource, String id) {
		super(String.format("%s with %s identifier not found", resource, id));
	}

	public ResourceNotFoundException(String resource, List<Long> idList) {
		super(String.format("%s with ids %s not found",
				resource,
				idList.stream()
						.map(String::valueOf)
						.collect(Collectors.joining(","))));
	}
}
